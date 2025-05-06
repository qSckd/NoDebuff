package me.funky.praxi.adapter.holograms;

import me.funky.praxi.Praxi;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.kit.KitLeaderboards;
import me.funky.praxi.leaderboard.Leaderboard;
import me.funky.praxi.util.hologram.Hologram;
import me.funky.praxi.util.LocationUtil;
import me.funky.praxi.util.TaskUtil;
import me.funky.praxi.util.CC;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HologramAdapter {
    private int currentLeaderboard = 0;
    @Getter private Hologram leaderBoardHologram;
    @Getter private Hologram welcomeHologram;

    public void HologramProvider() {
        registerHolograms();
    }

    private final Consumer<Hologram> updateConsumerElo = hologram -> {
        try {
            int i;
            List<Kit> leaderboardGames = Kit.getKits().stream().filter(kit -> kit.getGameRules().isRanked()).collect(Collectors.toList());
            if (leaderboardGames.isEmpty()) {
                for (int i2 = hologram.getLines().size() - 1; i2 >= 2; --i2) {
                    hologram.removeLine(i2);
                }
                hologram.getLine(2).updateLine("&7Currently, there are no players on the leaderboard.");
                return;
            }
            Kit gameMode = leaderboardGames.get(this.currentLeaderboard);
            if (hologram.getLines().size() >= 3) {
                hologram.getLine(3).updateLine("&b" + gameMode.getName());
            } else {
                hologram.addLine(3, "&b" + gameMode.getName());
            }
            List<KitLeaderboards> eloEntries = Leaderboard.getKitLeaderboards().get(gameMode.getName());
            for (i = hologram.getLines().size() - 1; i >= 4; --i) {
                hologram.removeLine(i);
            }
            if (eloEntries.isEmpty()) {
                hologram.addLine("&7Currently, there are no players on the leaderboard.");
            } else {
                i = 1;
                for (int j = 0; j < 10 && j < eloEntries.size(); j++) {
                    KitLeaderboards entry = eloEntries.get(j);
                    if (entry.getName() == null) {
                        continue;
                    }
                    hologram.addLine(i + 3, "&b" + i + ". " + CC.WHITE + entry.getName() + ChatColor.GRAY + " (" + "&b" + entry.getElo() + " ELO" + ChatColor.GRAY + ")");
                    ++i;
                }
            }
            for (i = hologram.getLines().size() - 1; i >= 15; --i) {
                hologram.removeLine(i);
            }
            ++this.currentLeaderboard;
            if (this.currentLeaderboard > leaderboardGames.size() - 1) this.currentLeaderboard = 0;
        } catch (Exception ignored) {
        }
    };

    /*private final Consumer<Hologram> updateConsumerWins = hologram -> {
        try {
            int i;
            List<Kit> leaderboardGames = Kit.getKits().stream().filter(kit -> kit.getGameRules().isRanked()).collect(Collectors.toList());
            if (leaderboardGames.isEmpty()) {
                for (int i2 = hologram.getLines().size() - 1; i2 >= 2; --i2) {
                    hologram.removeLine(i2);
                }
                hologram.getLine(2).updateLine("&7Currently, there are no players on the leaderboard.");
                return;
            }
            Kit gameMode = leaderboardGames.get(this.currentLeaderboard);
            if (hologram.getLines().size() >= 3) {
                hologram.getLine(3).updateLine("&b" + gameMode.getName());
            } else {
                hologram.addLine(3, "&b" + gameMode.getName());
            }
            List<KitLeaderboards> eloEntries = Leaderboard.getKitLeaderboards().get(gameMode.getName());
            for (i = hologram.getLines().size() - 1; i >= 4; --i) {
                hologram.removeLine(i);
            }
            if (eloEntries.isEmpty()) {
                hologram.addLine("&7Currently, there are no players on the leaderboard.");
            } else {
                i = 1;
                for (int j = 0; j < 10 && j < eloEntries.size(); j++) {
                    KitLeaderboards entry = eloEntries.get(j);
                    if (entry.getName() == null) {
                        continue;
                    }
                    hologram.addLine(i + 3, "&b" + i + ". " + CC.WHITE + entry.getName() + ChatColor.GRAY + " (" + "&b" + entry.getWins() + " ELO" + ChatColor.GRAY + ")");
                    ++i;
                }
            }
            for (i = hologram.getLines().size() - 1; i >= 15; --i) {
                hologram.removeLine(i);
            }
            ++this.currentLeaderboard;
            if (this.currentLeaderboard > leaderboardGames.size() - 1) this.currentLeaderboard = 0;
        } catch (Exception ignored) {
        }
    };*/

    public void registerWelcomeHologram() {
        if (LocationUtil.deserialize(Praxi.get().getHologramConfig().getString("MAIN.LOCATION")) == null || LocationUtil.deserialize(Praxi.get().getHologramConfig().getString("MAIN.LOCATION")).getWorld() == null) {
            return;
        }
        welcomeHologram = new Hologram(LocationUtil.deserialize(Praxi.get().getHologramConfig().getString("MAIN.LOCATION")));
        for (String line : Praxi.get().getHologramConfig().getStringList("MAIN.LINES")) {
            welcomeHologram.addLine(CC.translate(line));
        }
        Praxi.get().getHologramHandler().registerHologram(welcomeHologram);
    }

    public void registerEloLeaderboardHologram() {
        if (LocationUtil.deserialize(Praxi.get().getHologramConfig().getString("LEADERBOARDS.ELO.LOCATION")) == null || LocationUtil.deserialize(Praxi.get().getHologramConfig().getString("LEADERBOARDS.ELO.LOCATION")).getWorld() == null) {
            return;
        }
        leaderBoardHologram = new Hologram(LocationUtil.deserialize(Praxi.get().getHologramConfig().getString("LEADERBOARDS.ELO.LOCATION")));
        leaderBoardHologram.addLine(CC.translate("&b&lGlobal Elo"));
        leaderBoardHologram.addLine(CC.translate("&fLeaderboards"));
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&cLoading...");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        Praxi.get().getHologramHandler().registerHologram(leaderBoardHologram);
        TaskUtil.scheduleAtFixedRateOnPool(() -> updateConsumerElo.accept(leaderBoardHologram), 5, 5, TimeUnit.SECONDS);
    }

    /*public void registerWinsLeaderboardHologram() {
        if (LocationUtil.deserialize(Praxi.get().getHologramConfig().getString("LEADERBOARDS.WINS.LOCATION")) == null || LocationUtil.deserialize(Praxi.get().getHologramConfig().getString("LEADERBOARDS.WINS.LOCATION")).getWorld() == null) {
            return;
        }
        leaderBoardHologram = new Hologram(LocationUtil.deserialize(Praxi.get().getHologramConfig().getString("LEADERBOARDS.WINS.LOCATION")));
        leaderBoardHologram.addLine(CC.translate("&b&lGlobal Wins"));
        leaderBoardHologram.addLine(CC.translate("&fLeaderboards"));
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&cLoading...");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&f");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        leaderBoardHologram.addLine("&7");
        Praxi.get().getHologramHandler().registerHologram(leaderBoardHologram);
        TaskUtil.scheduleAtFixedRateOnPool(() -> updateConsumerWins.accept(leaderBoardHologram), 5, 5, TimeUnit.SECONDS);
    }*/

    public void registerHolograms() {
        this.registerWelcomeHologram();
        this.registerEloLeaderboardHologram();
        //this.registerWinsLeaderboardHologram();
    }

}
