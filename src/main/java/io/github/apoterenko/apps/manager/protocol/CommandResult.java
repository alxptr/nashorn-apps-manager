package io.github.apoterenko.apps.manager.protocol;

import com.google.gson.Gson;
import org.json.JSONObject;

import static io.github.apoterenko.apps.manager.Utils.*;

public class CommandResult extends BaseCommand {
    private String uuid;
    private Object data;

    public CommandResult() {
        this(null);
    }

    public CommandResult(String fromJSON) {
        if (fromJSON != null) {
            final CommandResult result = new Gson().fromJson(fromJSON, CommandResult.class);
            command = result.getCommand();
            uuid = result.getUuid();
            data = result.getData();
            consumer = result.getConsumer();
            producer = result.getProducer();
        }
    }

    @SuppressWarnings("unchecked")
    public CommandResult fromObject(Object o) throws Exception {
        if (o instanceof JSONObject) {
            final JSONObject jsonObject = (JSONObject) o;
            data = getObjectFromJSONObject(jsonObject, "data");
            consumer = getStringFromJSONObject(jsonObject, "consumer");
            producer = getStringFromJSONObject(jsonObject, "producer");
            uuid = getStringFromJSONObject(jsonObject, "uuid");
            command = getStringFromJSONObject(jsonObject, "command");
        } else if (o instanceof Command) {
            final Command command = (Command) o;
            this.command = command.getCommand();
            this.uuid = command.getUuid();
            this.consumer = command.getConsumer();
            this.producer = command.getProducer();
        }
        return this;
    }

    public Object getData() {
        return data;
    }

    public CommandResult setData(Object data) {
        this.data = data;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public CommandResult setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "uuid='" + uuid + '\'' +
                ", data=" + data +
                ", command='" + command + '\'' +
                ", consumer='" + consumer + '\'' +
                ", producer='" + producer + '\'' +
                '}';
    }
}