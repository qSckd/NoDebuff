package me.funky.praxi.util;

import me.funky.praxi.Praxi;
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
        Praxi.get().getServer().getScheduler().runTask(Praxi.get(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        try {
            Praxi.get().getServer().getScheduler().runTaskAsynchronously(Praxi.get(), runnable);
        } catch (IllegalStateException e) {
            Praxi.get().getServer().getScheduler().runTask(Praxi.get(), runnable);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        Praxi.get().getServer().getScheduler().runTaskTimer(Praxi.get(), runnable, delay, timer);
    }

    public static int runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(Praxi.get(), delay, timer);
        return runnable.getTaskId();
    }

    public static void runLater(Runnable runnable, long delay) {
        Praxi.get().getServer().getScheduler().runTaskLater(Praxi.get(), runnable, delay);
    }

    public static void runLaterAsync(Runnable runnable, long delay) {
        try {
            Praxi.get().getServer().getScheduler().runTaskLaterAsynchronously(Praxi.get(), runnable, delay);
        } catch (IllegalStateException e) {
            Praxi.get().getServer().getScheduler().runTaskLater(Praxi.get(), runnable, delay);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTimerAsync(Runnable runnable, long delay, long timer) {
        try {
            Praxi.get().getServer().getScheduler().runTaskTimerAsynchronously(Praxi.get(), runnable, delay, timer);
        } catch (IllegalStateException e) {
            Praxi.get().getServer().getScheduler().runTaskTimer(Praxi.get(), runnable, delay, timer);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTimerAsync(BukkitRunnable runnable, long delay, long timer) {
        Praxi.get().getServer().getScheduler().runTaskTimerAsynchronously(Praxi.get(), runnable, delay, timer);
    }

}
