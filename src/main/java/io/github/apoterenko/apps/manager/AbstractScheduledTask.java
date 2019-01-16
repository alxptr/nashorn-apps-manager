package io.github.apoterenko.apps.manager;

import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.concurrent.TimeUnit;

import static io.github.apoterenko.apps.manager.Utils.nvl;

public abstract class AbstractScheduledTask extends AbstractScheduledService {

    private static final int FIXED_RATE_TYPE = 0;
    private static final int FIXED_DELAY_TYPE = 1;
    private static final long DEFAULT_PERIOD = 5;

    private TimeUnit timeUnit;
    private long periodOrDelay;
    private long initialDelay;
    private int type;

    public AbstractScheduledTask(Long periodOrDelay, Long initialDelay, Integer type, TimeUnit timeUnit) {
        this.type = nvl(type, AbstractScheduledTask.FIXED_RATE_TYPE);
        this.timeUnit = nvl(timeUnit, TimeUnit.SECONDS);
        this.periodOrDelay = nvl(periodOrDelay, AbstractScheduledTask.DEFAULT_PERIOD);
        this.initialDelay = nvl(initialDelay, 0l);
    }

    public AbstractScheduledTask() {
        this(null, null, null, null);
    }

    public AbstractScheduledTask(Long periodOrDelay) {
        this(periodOrDelay, null, null, null);
    }

    public AbstractScheduledTask(Long periodOrDelay, Long initialDelay) {
        this(periodOrDelay, initialDelay, null, null);
    }

    public AbstractScheduledTask(Long periodOrDelay, Long initialDelay, Integer type) {
        this(periodOrDelay, initialDelay, type, null);
    }

    public Scheduler scheduler() {
        if (type == AbstractScheduledTask.FIXED_RATE_TYPE) {
            return Scheduler.newFixedRateSchedule(initialDelay, periodOrDelay, timeUnit);
        }
        return Scheduler.newFixedDelaySchedule(initialDelay, periodOrDelay, timeUnit);
    }
}