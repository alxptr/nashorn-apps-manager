package io.github.apoterenko.apps.manager.protocol;

import com.google.common.base.Joiner;
import io.github.apoterenko.apps.manager.Utils;
import org.json.JSONObject;

import static io.github.apoterenko.apps.manager.Utils.getStringFromJSONObject;

/**
 * Stable [21.05.2018]
 */
public class PayloadWrapper {
    private String type;
    private Object payload;

    /**
     * Stable [23.05.2018]
     */
    public PayloadWrapper() throws ClassNotFoundException {
        this(null);
    }

    /**
     * Stable [23.05.2018]
     *
     * @param payload payload
     */
    public PayloadWrapper(Object payload) {
        this.type = payload.getClass().getSimpleName();
        this.payload = payload;
    }

    /**
     * Stable [21.05.2018]
     *
     * @param fromJSON fromJSON
     * @throws ClassNotFoundException ClassNotFoundException
     */
    @SuppressWarnings("WeakerAccess")
    public PayloadWrapper(String fromJSON) throws ClassNotFoundException {
        if (fromJSON != null) {
            final PayloadWrapper payloadWrapper = Utils.fromJson(fromJSON, PayloadWrapper.class);
            if (payloadWrapper != null) {
                this.type = payloadWrapper.getType();
                this.payload = Utils.fromJson(Utils.toJson(payloadWrapper.getPayload()), getPayloadClass());
            }
        }
    }

    /**
     * Stable [24.05.2018]
     *
     * @param o o
     * @return PayloadWrapper
     * @throws Exception Exception
     */
    @SuppressWarnings("unchecked")
    public PayloadWrapper fromObject(Object o) throws Exception {
        if (o instanceof JSONObject) {
            final JSONObject jsonObject = (JSONObject) o;
            this.type = (getStringFromJSONObject(jsonObject, "type"));
            this.payload = new Command().fromObject(jsonObject.getJSONObject("payload"));
        }
        return this;
    }

    /**
     * Stable [24.05.2018]
     *
     * @return Class
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private Class<?> getPayloadClass() throws ClassNotFoundException {
        return Class.forName(Joiner.on(".").join(this.getClass().getPackage().getName(), type));
    }

    /**
     * Stable [21.05.2018]
     *
     * @return Type
     */
    @SuppressWarnings("WeakerAccess")
    public String getType() {
        return type;
    }

    /**
     * Stable [21.05.2018]
     *
     * @return Payload
     */
    @SuppressWarnings("WeakerAccess")
    public Object getPayload() {
        return payload;
    }

    /**
     * Stable [23.05.2018]
     *
     * @param type type
     * @return PayloadWrapper
     */
    public PayloadWrapper setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Stable [23.05.2018]
     *
     * @param payload payload
     * @return PayloadWrapper
     */
    public PayloadWrapper setPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    /**
     * Stable [21.05.2018]
     *
     * @return String
     */
    @Override
    public String toString() {
        return "PayloadWrapper{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }
}
