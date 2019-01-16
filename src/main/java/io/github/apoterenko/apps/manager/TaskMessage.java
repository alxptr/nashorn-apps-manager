package io.github.apoterenko.apps.manager;

public class TaskMessage {

    Task sourceTask;
    String targetTaskName;
    Object message;

    TaskMessage(Task sourceTask, Object message, String targetTaskName) {
        this.sourceTask = sourceTask;
        this.message = message;
        this.targetTaskName = targetTaskName;
    }

    TaskMessage(Task sourceTask, Object message) {
        this(sourceTask, message, null);
    }

    TaskMessage(Object message, String targetTaskName) {
        this(null, message, targetTaskName);
    }

    TaskMessage(Object message) {
        this(null, message, null);
    }

    public Task getSourceTask() {
        return sourceTask;
    }

    public String getTargetTaskName() {
        return targetTaskName;
    }

    public Object getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "TaskMessage{" +
                "message=" + message +
                ", targetTaskName='" + targetTaskName + '\'' +
                ", sourceTask=" + sourceTask +
                '}';
    }
}