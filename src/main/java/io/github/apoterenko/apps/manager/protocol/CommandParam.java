package io.github.apoterenko.apps.manager.protocol;

import org.json.JSONObject;

import static io.github.apoterenko.apps.manager.Utils.*;

public class CommandParam {
    private String paramName;
    private Object paramValue;

    public CommandParam() {
    }

    public CommandParam(String paramName, Object paramValue) {
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    @SuppressWarnings("unchecked")
    public CommandParam fromObject(Object o) throws Exception {
        if (o instanceof JSONObject) {
            final JSONObject jsonObject = (JSONObject) o;
            this.setParamName(getStringFromJSONObject(jsonObject, "paramName"));
            this.setParamValue(getObjectFromJSONObject(jsonObject, "paramValue"));
        }
        return this;
    }

    public CommandParam setParamName(String paramName) {
        this.paramName = paramName;
        return this;
    }

    public CommandParam setParamValue(Object paramValue) {
        this.paramValue = paramValue;
        return this;
    }

    public String getParamName() {
        return paramName;
    }

    public Object getParamValue() {
        return paramValue;
    }

    @Override
    public String toString() {
        return "CommandParam{" +
                "paramName='" + paramName + '\'' +
                ", paramValue=" + paramValue +
                '}';
    }
}