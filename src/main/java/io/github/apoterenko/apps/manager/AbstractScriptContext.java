package io.github.apoterenko.apps.manager;

import com.google.api.client.http.*;
import com.google.common.util.concurrent.ListenableFuture;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.SocketAddress;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractScriptContext {
    private static Logger logger = LoggerFactory.getLogger(AbstractScriptContext.class);

    private Task task;

    /**
     * Stable [25.05.2018]
     */
    protected AbstractScriptContext(Task task) {
        this.task = task;
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public AtomicReference getAtomicReference() {
        return new AtomicReference();
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public void sendMessage(Object message) {
        sendMessage(message, null);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public void sendMessage(Object message, String taskName) {
        postMessage(new TaskMessage(task, message, taskName));
    }

    /**
     * Stable [01.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public Task getCurrentTask() {
        return task;
    }

    /**
     * Stable [01.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public String getTaskName() {
        return task.getTaskName();
    }

    /**
     * Stable [11.06.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public boolean isProductionModeEnabled() {
        return Utils.isProductionModeEnabled();
    }

    /**
     * Stable [11.06.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public String getProxyHostProperty() {
        return Utils.nvl(System.getProperty(Constants.PROXY_HOST_PROPERTY), "127.0.0.1");
    }

    /**
     * Stable [11.06.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public String toJson(Object o) {
        return Utils.toJson(o);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public void logInfo(String s, Object... args) {
        logger.info(s, args);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public void logWarn(String s, Object... args) {
        logger.warn(s, args);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public void logError(String s, Object... args) {
        logger.error(s, args);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public void logDebug(String s, Object... args) {
        logger.debug(s, args);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public void logTrace(String s, Object... args) {
        logger.trace(s, args);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public void sleep(int value) throws InterruptedException {
        Thread.sleep(value == -1 ? Integer.MAX_VALUE : value);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public byte[] atob(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public byte[] btoa(byte[] data) {
        return Base64.getEncoder().encode(data);
    }

    /**
     * Stable [01.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public String join(String separator, List<?> data) {
        return Utils.join(separator, data);
    }

    /**
     * Stable [22.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public ServerConnector getServerConnector(Server server,
                                              SslConnectionFactory sslConnectionFactory,
                                              HttpConnectionFactory httpsConfig) {
        // This code doesn't work at JS because of lack of typings
        return new ServerConnector(server, sslConnectionFactory, httpsConfig);
    }

    /**
     * Stable [01.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public String getWorkingDirectory() {
        return Utils.getWorkingDirectory();
    }

    /**
     * Stable [01.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public String getWorkingTaskDirectory() {
        return Utils.toAbsoluteFilePath(getWorkingDirectory(), getTaskName());
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public BufferedImage readBufferedImage(byte[] data) throws IOException {
        return Utils.readBufferedImage(data);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public byte[] readFile(String fileName) throws IOException {
        return Utils.readFile(Utils.toFile(getWorkingTaskDirectory(), fileName));
    }

    /**
     * Stable [27.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    @Nullable
    public <T> T loadFile(String path, boolean asString) throws IOException {
        return Utils.loadFile(getHttpRequestFactory(), path, asString);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public File writeFile(byte[] data, String fileName) throws IOException {
        final String workingTaskDirectory = getWorkingTaskDirectory();

        Utils.createDirectoryIfNotExists(workingTaskDirectory);
        Utils.buildSubDirectories(workingTaskDirectory, fileName);

        final File file = Utils.toFile(workingTaskDirectory, fileName);
        Utils.writeFile(data, file);
        return file;
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    @Nullable
    public Object readIO(InputStream io, boolean asString) throws IOException {
        return Utils.readIO(io, asString);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    @Nullable
    public Object readIOAsString(InputStream io) throws IOException {
        return Utils.readIO(io, true);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public void removeFile(String fileName) throws IOException {
        Utils.removeFile(Utils.toFilePath(getWorkingTaskDirectory(), fileName));
    }

    /**
     * Stable [23.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public HttpResponse httpGet(String path) throws IOException {
        return Utils.buildGetRequest(getHttpRequestFactory(), path);
    }

    /**
     * Stable [14.08.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public HttpResponse httpPost(String path, String type, byte[] data) throws IOException {
        return Utils.buildPostRequest(getHttpRequestFactory(), path, type, data);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public void destroyTaskDirectory() throws IOException {
        Utils.destroyDirectoryRecursively(getWorkingTaskDirectory());
    }

    /**
     * Stable [01.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public TaskContext getExecutingTaskContext(String taskName) {
        return getExecutingTasks()
                .values()
                .stream()
                .filter(taskContext -> taskContext.task.getTaskName().equals(taskName))
                .findFirst()
                .get();
    }

    /**
     * Stable [14.06.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public SocketAddress makeSocketAddress(String host, int port) {
        return Utils.makeSocketAddress(host, port);
    }

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public abstract ListenableFuture spawnTask(ScriptObjectMirror callback, Object params);

    /**
     * Stable [01.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public abstract Map<Task, TaskContext> getExecutingTasks();

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    public abstract HttpRequestFactory getHttpRequestFactory();

    /**
     * Stable [25.05.2018]
     * Contract JavaScript method - can be called from App JS. Version 1.0
     */
    protected abstract void postMessage(TaskMessage message);
}