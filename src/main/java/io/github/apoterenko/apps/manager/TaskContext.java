package io.github.apoterenko.apps.manager;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import java.util.concurrent.Future;

public class TaskContext {
    Future<Boolean> future;
    Task task;
    ScriptEngine scriptEngine;

    TaskContext(ScriptEngine scriptEngine, Task task, Future<Boolean> future) {
        this.scriptEngine = scriptEngine;
        this.future = future;
        this.task = task;
    }

    public ScriptContext getScriptContext() {
        return scriptEngine.getContext();
    }

    @Override
    public String toString() {
        return "<Tasks status: [cancelled=" + future.isCancelled() + "], [is done=" + future.isDone() + "]>";
    }
}