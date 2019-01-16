package io.github.apoterenko.apps.manager.protocol;

/**
 * Stable [21.05.2018]
 */
public class ObjectStatus {
    private String object;
    private Object status;

    public String getObject() {
        return object;
    }

    public ObjectStatus setObject(String object) {
        this.object = object;
        return this;
    }

    public Object getStatus() {
        return status;
    }

    public ObjectStatus setStatus(Object status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "ObjectStatus{" +
                "object='" + object + '\'' +
                ", status=" + status +
                '}';
    }
}
