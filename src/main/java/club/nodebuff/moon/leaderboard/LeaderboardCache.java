package club.nodebuff.moon.leaderboard;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.kit.KitLeaderboards;
import club.nodebuff.moon.util.CC;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/* Author: Zatrex
   Project: Shadow
   Date: 2024/5/15
 */

public class LeaderboardCache {

    @Getter
    private static Map<Kit, List<String>> cachedLores = new HashMap<>();
    @Getter
    private static Map<Kit, List<String>> cachedWinsLores = new HashMap<>();
    @Getter
    private static Map<Kit, List<String>> cachedWinStreakLores = new HashMap<>();

    private enum LeaderboardsMode {
        ELO(KitLeaderboards::getElo, cachedLores),
        WINS(KitLeaderboards::getWins, cachedWinsLores),
        WINSTREAK(KitLeaderboards::getWinStreak, cachedWinStreakLores);

        private final Extractor<Integer> valueExtractor;
        private final Map<Kit, List<String>> cache;

        LeaderboardsMode(Extractor<Integer> valueExtractor, Map<Kit, List<String>> cache) {
            this.valueExtractor = valueExtractor;
            this.cache = cache;
        }

        public void setup(Kit kit, List<KitLeaderboards> leaderboard) {
            List<String> lore = new ArrayList<>();
            if (leaderboard != null) {
                List<KitLeaderboards> validEntries = leaderboard.stream()
                        .filter(entry -> valueExtractor.extract(entry) != null && entry.getName() != null)
                        .sorted(Comparator.comparing(valueExtractor::extract).reversed())
                        .collect(Collectors.toList());

                int pos = 1;
                for (int i = 0; i < 10 && i < validEntries.size(); i++) {
                    KitLeaderboards entry = validEntries.get(i);
                    lore.add("&b#" + pos + ": " + CC.RESET +
                            entry.getName() + " - " + CC.RESET +
                            valueExtractor.extract(entry));
                    pos++;
                }
            }
            cache.put(kit, new ArrayList<>(lore));
        }
    }

    private interface Extractor<T> {
        T extract(KitLeaderboards entry);
    }

    public static void setup(Kit kit, LeaderboardsMode mode) {
        mode.setup(kit, Leaderboard.getKitLeaderboards().get(kit.getName()));
    }

    public static void reload() {
        cachedLores.clear();
        cachedWinsLores.clear();
        cachedWinStreakLores.clear();
        for (Kit kit : Kit.getKits()) {
            setup(kit, LeaderboardsMode.ELO);
            setup(kit, LeaderboardsMode.WINS);
            setup(kit, LeaderboardsMode.WINSTREAK);
        }
    }
}