package game;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import utils.Logging;

public class Timer {
    private ScheduledThreadPoolExecutor ses;
    protected String file, name;
    private static final AtomicInteger threadNumber = new AtomicInteger(1);

    public void start() {
        if (ses != null && !ses.isShutdown() && !ses.isTerminated()) {
            return;
        }
        file = "Log_" + name + "_Except.rtf";
        ses = new ScheduledThreadPoolExecutor(5, new RejectedThreadFactory());
        ses.setKeepAliveTime(10, TimeUnit.MINUTES);
        ses.allowCoreThreadTimeOut(true);
        ses.setMaximumPoolSize(8);
        ses.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        //ses.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    }

    public ScheduledThreadPoolExecutor getSES() {
        return ses;
    }

    public void stop() {
        if (ses != null) {
            ses.shutdown();
        }
    }

    public ScheduledFuture<?> register(Runnable r, long repeatTime, long delay) {
        if (ses == null) {
            return null;
        }
        return ses.scheduleAtFixedRate(new LoggingSaveRunnable(r, file), delay, repeatTime, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> register(Runnable r, long repeatTime) {
        if (ses == null) {
            return null;
        }
        return ses.scheduleAtFixedRate(new LoggingSaveRunnable(r, file), 0, repeatTime, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> schedule(Runnable r, long delay) {
        if (ses == null) {
            return null;
        }
        return ses.schedule(new LoggingSaveRunnable(r, file), delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtTimestamp(Runnable r, long timestamp) {
        return schedule(r, timestamp - System.currentTimeMillis());
    }

    private static class LoggingSaveRunnable implements Runnable {

        Runnable r;
        public LoggingSaveRunnable(final Runnable r, final String file) {
            this.r = r;
        }

        @Override
        public void run() {
            try {
                r.run();
            } catch (Throwable t) {
                Logging.exceptionLog(t.getStackTrace());
            }
        }
    }

    private class RejectedThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber2 = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            final Thread t = new Thread(r);
            t.setName("Thread-" + threadNumber.getAndIncrement() + "-" + threadNumber2.getAndIncrement());
            return t;
        }
    }
}
