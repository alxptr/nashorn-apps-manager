package io.github.apoterenko.apps.manager;

import java.util.Objects;

public class Task {

    private String taskSource;
    private int taskVersion;
    private String taskName;

    public Task(String taskSource, String taskName, int taskVersion) {
        this.taskSource = taskSource;
        this.taskName = taskName;
        this.taskVersion = taskVersion;
    }

    public String getTaskSource() {
        return taskSource;
    }

    public int getTaskVersion() {
        return taskVersion;
    }

    public String getTaskName() {
        return taskName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Task task = (Task) o;
        return Objects.equals(taskName, task.taskName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName);
    }

    @Override
    public String toString() {
        return "<" + taskName + ":#" + taskVersion + ">";
    }
}