package club.nodebuff.moon.util;

import club.nodebuff.moon.Moon;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskUtil {

    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(8);

    public static void scheduleAtFixedRateOnPool(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(runnable, delay, period, timeUnit);
    }

    public static void scheduleOnPool(Runnable runnable, long delay, TimeUnit timeUnit) {
        scheduledThreadPoolExecutor.schedule(runnable, delay, timeUnit);
    }

    public static void executeWithPool(Runnable runnable) {
        scheduledThreadPoolExecutor.execute(runnable);
    }

    public static void run(Runnable runnable) {
        Moon.get().getServer().getScheduler().runTask(Moon.get(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        try {
            Moon.get().getServer().getScheduler().runTaskAsynchronously(Moon.get(), runnable);
        } catch (IllegalStateException e) {
            Moon.get().getServer().getScheduler().runTask(Moon.get(), runnable);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        Moon.get().getServer().getScheduler().runTaskTimer(Moon.get(), runnable, delay, timer);
    }

    public static int runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(Moon.get(), delay, timer);
        return runnable.getTaskId();
    }

    public static void runLater(Runnable runnable, long delay) {
        Moon.get().getServer().getScheduler().runTaskLater(Moon.get(), runnable, delay);
    }

    public static void runLaterAsync(Runnable runnable, long delay) {
        try {
            Moon.get().getServer().getScheduler().runTaskLaterAsynchronously(Moon.get(), runnable, delay);
        } catch (IllegalStateException e) {
            Moon.get().getServer().getScheduler().runTaskLater(Moon.get(), runnable, delay);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTimerAsync(Runnable runnable, long delay, long timer) {
        try {
            Moon.get().getServer().getScheduler().runTaskTimerAsynchronously(Moon.get(), runnable, delay, timer);
        } catch (IllegalStateException e) {
            Moon.get().getServer().getScheduler().runTaskTimer(Moon.get(), runnable, delay, timer);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTimerAsync(BukkitRunnable runnable, long delay, long timer) {
        Moon.get().getServer().getScheduler().runTaskTimerAsynchronously(Moon.get(), runnable, delay, timer);
    }

}
