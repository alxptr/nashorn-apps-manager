package io.github.apoterenko.apps.manager;

public class Config {
    private static final int DEFAULT_SERVICE_THREADS_POOL_COUNT = 3;
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String DEFAULT_MODE = "development";
    public static Config DEFAULT_CONFIG = new Config();

    private int serviceThreadsPoolCount;
    private String host;
    private String mode;

    /**
     * Stable [25.05.2018]
     */
    public Config() {
        this(
                DEFAULT_SERVICE_THREADS_POOL_COUNT,
                DEFAULT_HOST,
                DEFAULT_MODE
        );
    }

    /**
     * Stable [25.05.2018]
     *
     * @param serviceThreadsPoolCount serviceThreadsPoolCount
     * @param host host
     * @param mode mode
     */
    public Config(int serviceThreadsPoolCount, String host, String mode) {
        this.serviceThreadsPoolCount = serviceThreadsPoolCount;
        this.host = host;
        this.mode = mode;
    }

    public int getServiceThreadsPoolCount() {
        return serviceThreadsPoolCount;
    }

    public String getHost() {
        return host;
    }

    /**
     * Stable [25.05.2018]
     *
     * @return Production mode flag
     */
    public boolean isProductionMode() {
        return "production".equals(mode);
    }
}
