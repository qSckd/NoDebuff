package club.nodebuff.moon.event.game.map.impl;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.event.game.EventGame;
import club.nodebuff.moon.event.game.map.EventGameMap;
import club.nodebuff.moon.participant.GameParticipant;
import club.nodebuff.moon.participant.GamePlayer;
import club.nodebuff.moon.util.LocationUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpreadEventGameMap extends EventGameMap {

	@Getter private final List<Location> spawnLocations = new ArrayList<>();

	public SpreadEventGameMap(String mapName) {
		super(mapName);
	}

	@Override
	public void teleportFighters(EventGame game) {
		int i = 0;

		Location[] locations = spawnLocations.toArray(new Location[0]);

		for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
			for (GamePlayer gamePlayer : participant.getPlayers()) {
				Player player = gamePlayer.getPlayer();

				if (player != null) {
					player.teleport(locations[i]);

					i++;

					if (i == locations.length) {
						i = 0;
					}
				}
			}
		}
	}

	@Override
	public boolean isSetup() {
		return spectatorPoint != null && !spawnLocations.isEmpty();
	}

	@Override
	public void save() {
		super.save();

		FileConfiguration config = Moon.get().getEventsConfig().getConfiguration();
		config.set("EVENT_MAPS." + getMapName() + ".TYPE", "SPREAD");
		config.set("EVENT_MAPS." + getMapName() + ".SPAWN_LOCATIONS", spawnLocations
				.stream().map(LocationUtil::serialize).collect(Collectors.toList()));

		try {
			config.save(Moon.get().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
