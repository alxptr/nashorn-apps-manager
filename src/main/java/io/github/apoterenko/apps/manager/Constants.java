package io.github.apoterenko.apps.manager;

public final class Constants {
    public static final String TASK_STOP_HOOK_FN_NAME = "$$onTaskStopHook";
    public static final String TASK_MESSAGE_HOOK_FN_NAME = "$$onTaskMessageHook";
    public static final String TASK_CONTEXT = "$$taskContext";
    public static final String PACKAGE_CONTEXT_NAME = "$$packageContextName";
    public static final String COMMON_JS = "common.js";
    public static final String SCRIPT_ENGINE = "nashorn";
    public static final String TASK_JSON_PATH = "https://sweedpos.s3.amazonaws.com/%s/tasks.json";
    public static final String TASK_PATH = "https://sweedpos.s3.amazonaws.com/%s/%s-%d.js";
    public static final String TASK_TEMPLATE = "%s-%d.js";
    public static final String TASK_JSON_TEMPLATE = "tasks.json";
    public static final String CUSTOMER_PROPERTY = "$$Customer";
    public static final String USE_LOCAL_TASKS_PROPERTY = "$$UseLocalTasks";
    public static final String ACCESS_KEY_PROPERTY = "$$AccessKey";
    public static final String PRODUCTION_MODE_PROPERTY = "$$ProductionMode";
    public static final String PROXY_HOST_PROPERTY = "$$ProxyHost";
}