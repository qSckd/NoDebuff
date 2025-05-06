package club.nodebuff.moon.util;

import lombok.Getter;
import lombok.Setter;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.queue.Queue;
import club.nodebuff.moon.queue.QueueProfile;
import club.nodebuff.moon.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Cache {
    private final List<Queue> queues = new ArrayList<>();
    private final LinkedList<QueueProfile> players = new LinkedList<>();
    private final List<Match> matches = new ArrayList<>();
    private Location spawn = LocationUtil.deserialize(Moon.get().getSettingsConfig().getStringOrDefault("GENERAL.SPAWN-LOCATION", null));

    public Match getMatch(UUID matchUUID) {
        for (Match match : matches) {
            if (match.getMatchId().equals(matchUUID)) {
                return match;
            }
        }
        return null;
    }

	public static int getInQueues() {
        int inQueues = 0;

        for ( Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.isInQueue()) {
                inQueues++;
            }
        }

        return inQueues;
    }
}
