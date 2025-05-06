package club.nodebuff.moon.util;

import org.bukkit.ChatColor;

public class ProgressBar {

    public static String getBar(int current, int total) {
        if (total <= 0) {
            throw new IllegalArgumentException("Total must be greater than 0."); // debug
        }
        current = Math.max(0, current);
        return getProgressBar(current, total, 8, ChatColor.GREEN, ChatColor.GRAY);
    }

    public static String getProgressBar(int current, int max, int totalBars, ChatColor completedColor, ChatColor notCompletedColor) {
        if (max <= 0) {
            throw new IllegalArgumentException("Max must be greater than 0."); // debug
        }

        current = Math.min(current, max);

        float percent = (float) current / max;
        int filledBars = Math.round(totalBars * percent);

        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < totalBars; i++) {
            if (i < filledBars) {
                progressBar.append(completedColor).append("■");
            } else {
                progressBar.append(notCompletedColor).append("■");
            }
        }

        int percentage = Math.round(percent * 100);

        return progressBar.toString() + ChatColor.GRAY + " " + percentage + "%";
    }

    public static String getBarMenu(int current, int total) {
        if (total <= 0) {
            throw new IllegalArgumentException("Total must be greater than 0."); // debug
        }
        current = Math.max(0, current);
        return getProgressBarMenu(current, total, 8, ChatColor.GREEN, ChatColor.GRAY);
    }

    public static String getProgressBarMenu(int current, int max, int totalBars, ChatColor completedColor, ChatColor notCompletedColor) {
        if (max <= 0) {
            throw new IllegalArgumentException("Max must be greater than 0."); // debug
        }

        current = Math.min(current, max);

        float percent = (float) current / max;
        int filledBars = Math.round(totalBars * percent);

        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < totalBars; i++) {
            if (i < filledBars) {
                progressBar.append(completedColor).append("\u258e");
            } else {
                progressBar.append(notCompletedColor).append("\u258e");
            }
        }

        int percentage = Math.round(percent * 100);

        return progressBar.toString() + ChatColor.GRAY + " " + percentage + "%";
    }
}
