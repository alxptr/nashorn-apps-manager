package io.github.apoterenko.apps.manager.protocol;

import com.google.common.collect.Maps;
import org.json.JSONObject;

import java.util.*;

import static io.github.apoterenko.apps.manager.Utils.getStringFromJSONObject;

public class Command extends BaseCommand {

    private String uuid = UUID.randomUUID().toString();
    private Map<String, CommandParam> params = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    public Command fromObject(Object o) throws Exception {
        if (o instanceof JSONObject) {
            final JSONObject jsonObject = (JSONObject) o;

            this.setUuid(getStringFromJSONObject(jsonObject, "uuid"));
            this.setCommand(getStringFromJSONObject(jsonObject, "command"));
            this.setConsumer(getStringFromJSONObject(jsonObject, "consumer"));
            this.setProducer(getStringFromJSONObject(jsonObject, "producer"));

            if (jsonObject.has("params")) {
                final JSONObject params = jsonObject.getJSONObject("params");
                for (final Iterator it = params.keys(); it.hasNext(); ) {
                    final String key = (String) it.next();
                    this.addParam(new CommandParam().fromObject(params.getJSONObject(key)));
                }
            }
        }
        return this;
    }

    public Command setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public Command addParam(CommandParam param) {
        params.put(param.getParamName(), param);
        return this;
    }

    public Command addParam(String paramName, Object paramValue) {
        this.addParam(new CommandParam(paramName, paramValue));
        return this;
    }

    public Command addParams(Map<String, CommandParam> params) {
        this.params.putAll(params);
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public Map<String, CommandParam> getParams() {
        return params;
    }

    public CommandParam getParamByName(String paramName) {
        return params.get(paramName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Command setCommand(String command) {
        return super.setCommand(command);
    }

    @Override
    public String toString() {
        return "Command{" +
                "uuid='" + uuid + '\'' +
                ", params=" + params +
                ", command='" + command + '\'' +
                ", consumer='" + consumer + '\'' +
                ", producer='" + producer + '\'' +
                '}';
    }
}