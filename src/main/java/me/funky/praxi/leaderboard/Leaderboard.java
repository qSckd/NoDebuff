package club.nodebuff.moon.leaderboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.kit.KitLeaderboards;
import club.nodebuff.moon.util.TaskUtil;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

/* Author: Zatrex
   Project: Shadow
   Date: 2024/5/15
 */
 
// TODO: Recode leaderboards menu (dont recode stats)

public class Leaderboard {

    @Getter public static Map<String, List<KitLeaderboards>> kitLeaderboards = Maps.newHashMap();
    @Getter public static Map<UUID, Integer> leaderboards = Maps.newHashMap();

    public static void init() {
        TaskUtil.runTimerAsync(Leaderboard::updateLeaderboards, 0L, 600L);
        TaskUtil.runTimerAsync(new BukkitRunnable() {
            @Override
            public void run() {
                for (Kit kit : Kit.getKits()) {
                    kit.updateKitLeaderboards();
                }
            }
        }, 0L, 600L);
        LeaderboardCache.reload();
    }

    private static List<Document> getPlayersSortByLadderElo(Kit ladder) {
        try {
            Document sort = new Document();
            sort.put("kitStatistics." + ladder.getName() + ".elo", -1);
            return Moon.get().getMongoDatabase().getCollection("profiles").find().sort(sort).limit(10).into(new ArrayList<>());
        } catch (Exception ex) {
            return null;
        }
    }

    private static List<Document> getPlayersSortByLadderWins(Kit ladder) {
        try {
            Document sort = new Document();
            sort.put("kitStatistics." + ladder.getName() + ".won", -1);
            return Moon.get().getMongoDatabase().getCollection("profiles").find().sort(sort).limit(10).into(new ArrayList<>());
        } catch (Exception ex) {
            return null;
        }
    }

    public static void updateLeaderboards() {
        long startTime = System.currentTimeMillis();
        TaskUtil.runAsync(new BukkitRunnable() {
            @Override
            public void run() {
                leaderboards.clear();

                for (Document value : Moon.get().getMongoDatabase().getCollection("profiles").find()) {
                    UUID uuid = UUID.fromString(value.getString("uuid"));
                    Document kitStatistics = (Document) value.get("kitStatistics");

                    int allElos = 0;
                    for (String key : kitStatistics.keySet()) {
                        Document kitDocument = (Document) kitStatistics.get(key);
                        Kit kit = Kit.getByName(key);

                        if (kit != null && kit.getGameRules().isRanked()) {
                            Integer elo = (kitDocument != null) ? kitDocument.getInteger("elo") : null;

                            elo = (elo != null) ? elo : 1000;
                            allElos += elo;
                        }
                    }
                    leaderboards.put(uuid, allElos);
                }
                kitLeaderboards.clear();
                Kit.getKits().stream().filter(kit -> kit.getGameRules().isRanked()).forEach(kit -> {
                    List<KitLeaderboards> entry = Lists.newArrayList();
                    List<Document> sortedPlayers = getPlayersSortByLadderElo(kit);

                    if (sortedPlayers != null) {
                        sortedPlayers.forEach(document -> {
                            UUID uuid = UUID.fromString(document.getString("uuid"));
                            Document kitStatistics = (Document) document.get("kitStatistics");
                            Document kitDocument = (Document) kitStatistics.get(kit.getName());
                            Integer elo = (kitDocument != null) ? kitDocument.getInteger("elo") : null;
                            Integer winstreak = (kitDocument != null) ? kitDocument.getInteger("winstreak") : null;
                            Integer wins = (kitDocument != null) ? kitDocument.getInteger("won") : null;

                            elo = (elo != null) ? elo : 1000;
                            winstreak = (winstreak != null) ? winstreak : 0;
                            wins = (wins != null) ? wins : 0;

                            entry.add(new KitLeaderboards(Bukkit.getOfflinePlayer(uuid).getName(), elo, wins, winstreak));
                        });
                        kitLeaderboards.put(kit.getName(), entry);
                    }
                });

                Comparator<Map.Entry<UUID, Integer>> comparator1 = Map.Entry.comparingByValue();
                Leaderboard.leaderboards = leaderboards.entrySet().stream()
                        .sorted(comparator1.reversed())
                        .limit(10)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
                LeaderboardCache.reload();
                long endTime = System.currentTimeMillis();
                long finalTime = endTime - startTime;
                Moon.get().broadcastMessage(Moon.get().getMainConfig().getString("LEADERBOARDS.UPDATE-MESSAGE"));
            }
        });
    }

}
