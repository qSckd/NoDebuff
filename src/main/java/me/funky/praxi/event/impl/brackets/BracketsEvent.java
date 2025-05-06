package club.nodebuff.moon.event.impl.brackets;

import club.nodebuff.moon.util.config.BasicConfigurationFile;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.event.Event;
import club.nodebuff.moon.event.game.EventGame;
import club.nodebuff.moon.event.game.EventGameLogic;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* Author: HmodyXD
   Project: Shadow
   Date: 2024/5/13
 */

public class BracketsEvent implements Event {

	@Setter private Location lobbyLocation;
	@Getter private final List<String> allowedMaps;

	public BracketsEvent() {
		BasicConfigurationFile config = Moon.get().getEventsConfig();

		lobbyLocation = LocationUtil.deserialize(config.getString("EVENTS.BRACKETS.LOBBY_LOCATION"));

		allowedMaps = new ArrayList<>();
		allowedMaps.addAll(config.getStringList("EVENTS.BRACKETS.ALLOWED_MAPS"));
	}

	@Override
	public String getDisplayName() {
		return "Brackets";
	}

	@Override
	public String getDisplayName(EventGame game) {
		return "Brackets &7(Solos)";
	}


	@Override
	public List<String> getDescription() {
		return Arrays.asList("Fight random players, beat",
	                         "your opponents in a 1v1.",
		                     "The last player standing wins!");
	}

	@Override
	public Location getLobbyLocation() {
		return lobbyLocation;
	}

	@Override
	public ItemStack getIcon() {
		return new ItemBuilder(Material.DIAMOND_SWORD).build();
	}

	@Override
	public boolean canHost(Player player) {
		return player.hasPermission("moon.event.host.brackets");
	}

	@Override
	public List<Listener> getListeners() {
		return Collections.emptyList();
	}

	@Override
	public List<Object> getCommands() {
		return Collections.emptyList();
	}

	@Override
	public EventGameLogic start(EventGame game) {
		return new BracketsGameLogic(game);
	}

	@Override
	public void save() {
		FileConfiguration config = Moon.get().getEventsConfig().getConfiguration();
		config.set("EVENTS.BRACKETS.LOBBY_LOCATION", LocationUtil.serialize(lobbyLocation));
		config.set("EVENTS.BRACKETS.ALLOWED_MAPS", allowedMaps);

		try {
			config.save(Moon.get().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}