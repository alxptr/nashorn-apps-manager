package io.github.apoterenko.apps.manager;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.util.concurrent.Executors;

public class Module extends AbstractModule {
    private static Logger logger = LoggerFactory.getLogger(Module.class);

    @Override
    protected void configure() {
        final Config config = getConfig();
        if (config.getHost() != null) {
            System.setProperty(Constants.PROXY_HOST_PROPERTY, config.getHost());
        }
        if (config.isProductionMode()) {
            System.setProperty(Constants.PRODUCTION_MODE_PROPERTY, config.isProductionMode() ? "true" : "false");
            logger.info("Production mode is enabled.");
        }

        logger.info(
                "The module is being configured...\n" +
                        "Current host: {}\n" +
                        "Current directory: {}\n" +
                        "Current service threads pool count: {}",
                config.getHost(),
                Utils.getCurrentDirectory(),
                config.getServiceThreadsPoolCount()
        );

        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        bind(ListeningExecutorService.class).annotatedWith(ScriptTasksPool.class).toInstance(
                MoreExecutors.listeningDecorator(Executors.newCachedThreadPool())
        );
        bind(ListeningExecutorService.class).annotatedWith(ScriptServicesTaskPool.class).toInstance(
                MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(config.getServiceThreadsPoolCount()))
        );
        bind(EventBus.class).annotatedWith(EventsChannel.class).toInstance(new EventBus());
        bind(EventBus.class).annotatedWith(MessagesChannel.class).toInstance(new EventBus());
        bind(ScriptEngineManager.class).toInstance(scriptEngineManager);
        bind(HttpRequestFactory.class).toInstance(new NetHttpTransport().createRequestFactory());
    }

    /**
     * Stable [25.05.2018]
     *
     * @return Config
     */
    private Config getConfig() {
        try {
            final Config cfg = Utils.loadConfigFile();
            return cfg != null ? cfg : Config.DEFAULT_CONFIG;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}