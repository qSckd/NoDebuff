package me.funky.praxi.event.impl.gulag;

import lombok.Getter;
import lombok.Setter;
import me.funky.praxi.Praxi;
import me.funky.praxi.event.Event;
import me.funky.praxi.event.game.EventGame;
import me.funky.praxi.event.game.EventGameLogic;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.LocationUtil;
import me.funky.praxi.util.config.BasicConfigurationFile;
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

public class GulagEvent implements Event {

	@Setter private Location lobbyLocation;
	@Getter private final List<String> allowedMaps;

	public GulagEvent() {
		BasicConfigurationFile config = Praxi.get().getEventsConfig();

		lobbyLocation = LocationUtil.deserialize(config.getString("EVENTS.GULAG.LOBBY_LOCATION"));

		allowedMaps = new ArrayList<>();
		allowedMaps.addAll(config.getStringList("EVENTS.GULAG.ALLOWED_MAPS"));
	}

    @Override
    public String getDisplayName() {
        return "Gulag";
    }

    @Override
    public String getDisplayName(EventGame game) {
        return "Gulag &7(Solos)";
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "Compete to be last on",
                "the 1v1 platform."
        );
    }


	@Override
	public Location getLobbyLocation() {
		return lobbyLocation;
	}

	@Override
	public ItemStack getIcon() {
		return new ItemBuilder(Material.IRON_FENCE).build();
	}

	@Override
	public boolean canHost(Player player) {
		return player.hasPermission("moon.event.host.gulag");
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
		return new GulagGameLogic(game);
	}

	@Override
	public void save() {
		FileConfiguration config = Praxi.get().getEventsConfig().getConfiguration();
		config.set("EVENTS.GULAG.LOBBY_LOCATION", LocationUtil.serialize(lobbyLocation));
		config.set("EVENTS.GULAG.ALLOWED_MAPS", allowedMaps);

		try {
			config.save(Praxi.get().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
