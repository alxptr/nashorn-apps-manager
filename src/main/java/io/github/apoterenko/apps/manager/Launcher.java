package io.github.apoterenko.apps.manager;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Launcher {
    private static Logger logger = LoggerFactory.getLogger(Launcher.class);

    /**
     * Stable [06.08.2018]
     *
     * @param args args
     */
    public static void main(String[] args) {
        final Map<String, String> flags = Utils.getFlags(args);
        final String customer = flags.get("customer");

        if (!flags.containsKey("accessKey")) {
            throw new RuntimeException("The accessKey flag is not defined!");
        }
        if (!flags.containsKey("customer")) {
            throw new RuntimeException("The customer flag is not defined!");
        }
        System.setProperty(Constants.ACCESS_KEY_PROPERTY, flags.get("accessKey"));
        System.setProperty(Constants.CUSTOMER_PROPERTY, customer);

        logger.info("Customer flag: " + customer);

        if (flags.containsKey("use-local-tasks")) {
            System.setProperty(Constants.USE_LOCAL_TASKS_PROPERTY, "true");
            logger.info("Need to use JS-tasks from a jar.");
        }

        final Injector injector = Guice.createInjector(new Module());
        injector.getInstance(LauncherService.class).launch();
    }
}