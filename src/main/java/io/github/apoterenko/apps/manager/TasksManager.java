package io.github.apoterenko.apps.manager;

import com.google.api.client.http.HttpRequestFactory;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import javax.swing.event.ChangeEvent;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class TasksManager {
    private static Logger logger = LoggerFactory.getLogger(TasksManager.class);

    @Inject private WorkingDirectory workingDirectory;
    @Inject private HttpRequestFactory httpRequestFactory;
    @Inject private ScriptEngineManager scriptEngineManager;
    @Inject @ScriptTasksPool private ListeningExecutorService scriptTasksPool;
    @Inject @ScriptServicesTaskPool private ListeningExecutorService scriptServicesTaskPool;
    @Inject @MessagesChannel private EventBus messagesChannel;

    private Map<Task, TaskContext> tasksMap = new ConcurrentHashMap<>();
    private AtomicInteger actualAliveThreadsCount = new AtomicInteger(0);    // For debugging

    @Inject
    private void init() {
        messagesChannel.register(this);
    }

    @Subscribe
    public void onTasksMessages(ChangeEvent event) {
        processTaskMessage((TaskMessage) event.getSource());
    }

    /**
     * Start the tasks synchronously
     * @param tasks Tasks
     */
    public void startTasksSynchronously(List<Task> tasks) {
        tasks.forEach(this::startTaskSynchronously);
    }

    /**
     * Start the task synchronously
     * @param newTask Task
     */
    public void startTaskSynchronously(Task newTask) {
        final TaskContext previousTaskContext = tasksMap.get(newTask);
        if (previousTaskContext != null) {
            final Task previousTask = previousTaskContext.task;
            if (isTaskNeededToBeReplaced(newTask, previousTask)) {
                stopTaskSynchronously(previousTaskContext);  // A task is identified by name (hashCode+equals)
                logger.info("The task {} has been stopped because new task {}. Tasks map size: {}, actual size: {}",
                        new Object[]{previousTask, newTask, tasksMap.size(), actualAliveThreadsCount.get()});
            }
        }
        executeTask(newTask);
    }

    /**
     * Stop the task synchronously
     * @param task Task
     */
    public void stopTaskSynchronously(Task task) {
        final TaskContext taskContext = tasksMap.get(task);
        if (taskContext == null) {
            return;
        }
        stopTaskSynchronously(taskContext);
    }

    /**
     * Stop the task synchronously
     * @param taskContext Task context
     */
    public void stopTaskSynchronously(final TaskContext taskContext) {
        interruptTask(taskContext);

        if (!taskContext.future.isDone() || !taskContext.future.isCancelled()) {
            try {
                // The thread executing this task should be interrupted
                taskContext.future.cancel(true);
            } catch (Exception e) {
                logger.error("Can't cancel the task {}", taskContext.task);
            }
        }
    }

    /**
     * Send a message to the task/tasks
     * @param message Message
     */
    public void sendMessage(TaskMessage message) {
        messagesChannel.post(new ChangeEvent(message));
    }

    private void interruptTask(TaskContext taskContext) {
        invokeJsMethod(taskContext, Constants.TASK_STOP_HOOK_FN_NAME);
    }

    private void sendMessageToTask(TaskContext taskContext, TaskMessage taskMessage) {
        invokeJsMethod(taskContext, Constants.TASK_MESSAGE_HOOK_FN_NAME, taskMessage);
    }

    /**
     * Stable [21.05.2018]
     *
     * @param taskContext
     * @param method
     * @param args
     */
    private void invokeJsMethod(TaskContext taskContext, String method, Object... args) {
        final ScriptEngine scriptEngine = taskContext.scriptEngine;
        final Invocable invocable = (Invocable) scriptEngine;

        if (scriptEngine.getContext().getAttribute(method) == null) {
            logger.debug("The method {} of task {} with args {} is not exists.",
                    new Object[]{method, taskContext.task, args});
            return;
        }
        try {
            final Object result = invocable.invokeFunction(method, args);
            logger.debug("The method {} of task {} with args {} has been invoked successfully with result {}.",
                    new Object[]{method, taskContext.task, args, result});
        } catch (Exception e) {
            logger.error("The method {} of task {} with args {} has been invoked unsuccessfully. Exception: {}",
                    new Object[]{method, taskContext.task, args, e.getMessage()});
        }
    }

    private void executeTask(final Task task) {
        final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(Constants.SCRIPT_ENGINE);

        final ListenableFuture<Boolean> currentTaskFuture = scriptTasksPool.submit(() -> {
            logger.info("The task {} is starting.... Tasks map size: {}, actual size: {}",
                    new Object[]{task, tasksMap.size(), actualAliveThreadsCount.incrementAndGet()});

            scriptEngine.getContext().setAttribute(
                    Constants.TASK_CONTEXT,
                    new ScriptContext(task),
                    javax.script.ScriptContext.ENGINE_SCOPE
            );
            final String taskSource = task.getTaskSource();
            final String commonModule = Utils.readResourceAsString(Constants.COMMON_JS);
            if (commonModule == null) {
                throw new Exception("The common module is not defined!");
            }

            scriptEngine.getContext().setAttribute(
                    Constants.PACKAGE_CONTEXT_NAME,
                    this.getClass().getPackage().getName(),
                    javax.script.ScriptContext.ENGINE_SCOPE
            );

            try {
                scriptEngine.eval(commonModule);
            } catch (Exception e) {
                logger.error("Can't eval the common source code. {}", e.getMessage());
                return false;
            }
            try {
                scriptEngine.eval(taskSource);
            } catch (Exception e) {
                logger.error("Can't eval the task {}. {}", task, e.getMessage());
                return false;
            }
            return true;
        });

        Futures.addCallback(currentTaskFuture, new FutureCallback<Boolean>() {

            public void onSuccess(Boolean result) {
                removeTask();
                if (result) {
                    logger.info("The task {} has been finished successfully", task);
                } else {
                    logger.warn("The task {} has been finished unsuccessfully", task);
                }
            }

            @Override
            public void onFailure(Throwable var1) {
                removeTask();
                if (!(var1 instanceof CancellationException)) {
                    logger.error("The task {} has been terminated with errors. {}", task, var1.getMessage());
                } else {
                    logger.trace("The task {} has been terminated with errors. {}", task, var1.getMessage());
                }
            }

            private void removeTask() {
                actualAliveThreadsCount.decrementAndGet();
                final TaskContext taskContext = tasksMap.get(task);
                if (taskContext.task.getTaskVersion() == task.getTaskVersion()) {
                    tasksMap.remove(task);
                } else {
                    logger.error("There is inconsistent state of the tasks {} and {}", taskContext.task, task);
                }
            }
        });

        tasksMap.put(task, new TaskContext(scriptEngine, task, currentTaskFuture));
    }

    private void processTaskMessage(TaskMessage taskMessage) {
        final Task sourceTask = taskMessage.sourceTask;
        final String targetTaskName = taskMessage.targetTaskName;
        boolean isMessageSentToTargetTask = false;

        for (final Map.Entry<Task, TaskContext> entry : tasksMap.entrySet()) {
            final Task currentTask = entry.getKey();
            final TaskContext currentTaskContext = entry.getValue();
            final Future<Boolean> currentTaskFuture = currentTaskContext.future;

            if (currentTaskFuture.isDone()) {
                try {
                    logger.info(
                            "The task {} had been done with result {}. Nothing to do", currentTask,
                            currentTaskFuture.get()
                    );
                } catch (Exception error) {
                    logger.error("An exception occurred while getting the result. The task is {}", currentTask);
                }
                continue;
            }

            if (targetTaskName != null) {
                if (currentTask.getTaskName().equals(targetTaskName)) {
                    sendMessageToTask(currentTaskContext, taskMessage);
                    isMessageSentToTargetTask = true;
                }
                continue;
            }
            if (currentTask.equals(sourceTask)) {
                // Don't need to post to yourself
                continue;
            }
            sendMessageToTask(currentTaskContext, taskMessage);
        }

        if (targetTaskName != null && !isMessageSentToTargetTask) {
            logger.warn("The system has not been able to send a message {} from {} to {}",
                    new Object[]{taskMessage, sourceTask, targetTaskName});
        }
    }

    private boolean isTaskNeededToBeReplaced(final Task newTask, final Task previousTask) {
        return newTask.getTaskVersion() > previousTask.getTaskVersion();
    }

    public class ScriptContext extends AbstractScriptContext {
        ScriptContext(Task task) {
            super(task);
        }

        @Override
        public ListenableFuture spawnTask(ScriptObjectMirror callback, Object params) {
            return scriptServicesTaskPool.submit(() -> callback.call(null, params));
        }

        /**
         * Contract JavaScript method - can be called from App JS. Version 1.0
         */
        @Override
        public Map<Task, TaskContext> getExecutingTasks() {
            return Collections.unmodifiableMap(tasksMap);
        }

        /**
         * Contract JavaScript method - can be called from App JS. Version 1.0
         */
        @Override
        public HttpRequestFactory getHttpRequestFactory() {
            return httpRequestFactory;
        }

        protected void postMessage(TaskMessage message) {
            TasksManager.this.sendMessage(message);
        }
    }
}