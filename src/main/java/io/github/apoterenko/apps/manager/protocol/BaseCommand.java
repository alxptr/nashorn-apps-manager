package io.github.apoterenko.apps.manager.protocol;

public class BaseCommand {
    protected String command;
    protected String consumer;
    protected String producer;

    public String getConsumer() {
        return consumer;
    }

    public String getProducer() {
        return producer;
    }

    public String getCommand() {
        return command;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseCommand> T setConsumer(String consumer) {
        this.consumer = consumer;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseCommand> T setProducer(String producer) {
        this.producer = producer;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseCommand> T setCommand(String command) {
        this.command = command;
        return (T) this;
    }
}