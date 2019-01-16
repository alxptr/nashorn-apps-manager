package io.github.apoterenko.apps.manager;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.repackaged.com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.ChangeEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Singleton
public class LauncherService {
    private static Logger logger = LoggerFactory.getLogger(LauncherService.class);

    @Inject private TasksManager tasksManager;
    @Inject @EventsChannel private EventBus eventBus;
    @Inject private HttpRequestFactory httpRequestFactory;

    @Inject
    private void init() {
        eventBus.register(this);
    }

    @Subscribe
    @SuppressWarnings("unchecked")
    public void onLaunchTasks(ChangeEvent e) {
        final List<Task> tasks = (List<Task>) e.getSource();
        tasksManager.startTasksSynchronously(tasks);
    }

    public void launch() {
        loadTasksAndExecute();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                // TODO
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void loadTasksAndExecute() {
        final Map<String, Map<String, String>> customerConfig = getTasksConfig();
        final List<Task> tasks = Lists.newArrayList();

        for (Map.Entry<String, Map<String, String>> entry : customerConfig.entrySet()) {
            final String taskName = entry.getKey();
            final Map<String, String> settings = entry.getValue();
            final int version = Integer.parseInt(settings.get("version"));
            final int sourceHash = Integer.parseInt(settings.get("hash"));

            final String localTask = String.format(Constants.TASK_TEMPLATE, taskName, version);
            final String cloudTask = String.format(Constants.TASK_PATH, getAccessKey(), taskName, version);

            final String script = Utils.isNeedToUseLocalTasks()
                    ? readResourceAsString(localTask)
                    : loadFileAsString(cloudTask);
            final int currentSourceHash = Objects.hashCode(script);

            if (Utils.isNeedToUseLocalTasks()) {
                logger.info("The task {} has been read from resources successfully. Hash: {}.",
                        localTask, currentSourceHash);
            } else {
                logger.info("The task {} has been loaded from cloud successfully. Hash: {}.",
                        cloudTask, currentSourceHash);
            }

            if (sourceHash != currentSourceHash) {
                throw new RuntimeException(
                        String.format(
                                "The calculated hash %d is not equal to task config hash %d. Task: %s. Customer: %s",
                                currentSourceHash,
                                sourceHash,
                                taskName,
                                getCurrentCustomer()
                        )
                );
            }

            tasks.add(new Task(script, taskName, version));
        }

        eventBus.post(new ChangeEvent(tasks));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, String>> getTasksConfig() {
        final Map<String, Object> tasksJSON = Utils.fromJson(
                Utils.isNeedToUseLocalTasks()
                        ? readResourceAsString(Constants.TASK_JSON_TEMPLATE)
                        : loadFileAsString(String.format(Constants.TASK_JSON_PATH, getAccessKey()))
        );
        if (tasksJSON == null) {
            throw new RuntimeException("Tasks json file is undefined.");
        }

        final Map<String, Map<String, String>> customerConfig =
                (Map<String, Map<String, String>>) tasksJSON.get(getCurrentCustomer());
        if (customerConfig == null) {
            throw new RuntimeException("Config does not exist to a customer.");
        }
        return customerConfig;
    }

    /**
     * Stable [27.05.2018]
     *
     * @param filePath filePath
     * @return File as string
     */
    private String loadFileAsString(String filePath) {
        try {
            return Utils.loadFile(httpRequestFactory, filePath, true);
        } catch (IOException e) {
            final String errorMessage = "Cannot load the file by path " + filePath + ". Exception: " + e.getMessage();
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Stable [11.06.2018]
     *
     * @param resource resource
     * @return Resource as string
     */
    private String readResourceAsString(String resource) {
        try {
            return Utils.readResourceAsString(resource);
        } catch (IOException e) {
            final String errorMessage = "Cannot read the resource " + resource + ". Exception: " + e.getMessage();
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Stable [27.05.2018]
     *
     * @return Current customer
     */
    private String getCurrentCustomer() {
        return System.getProperty(Constants.CUSTOMER_PROPERTY);
    }

    /**
     * Stable [27.05.2018]
     *
     * @return Access key
     */
    private String getAccessKey() {
        return System.getProperty(Constants.ACCESS_KEY_PROPERTY);
    }
}