package me.funky.praxi.event;

import me.funky.praxi.Praxi;
import me.funky.praxi.event.game.EventGame;
import me.funky.praxi.event.game.EventGameLogic;
import me.funky.praxi.event.impl.sumo.SumoEvent;
import me.funky.praxi.event.impl.spleef.SpleefEvent;
import me.funky.praxi.event.impl.brackets.BracketsEvent;
import me.funky.praxi.event.impl.gulag.GulagEvent;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public interface Event {

	List<Event> events = new ArrayList<>();

	static void init() {
		events.add(new SumoEvent());
		events.add(new SpleefEvent());
		events.add(new BracketsEvent());
        events.add(new GulagEvent());

		for (Event event : events) {
			for (Listener listener : event.getListeners()) {
				Praxi.get().getServer().getPluginManager().registerEvents(listener, Praxi.get());
			}

			for (Object command : event.getCommands()) {
				Praxi.get().getHoncho().registerCommand(command);
			}
		}
	}

	static <T extends Event> T getEvent(Class<? extends Event> clazz) {
		for (Event event : events) {
			if (event.getClass() == clazz) {
				return (T) clazz.cast(event);
			}
		}

		return null;
	}

	String getDisplayName();

	String getDisplayName(EventGame game);

	List<String> getDescription();

	Location getLobbyLocation();

	void setLobbyLocation(Location location);

	ItemStack getIcon();

	boolean canHost(Player player);

	List<String> getAllowedMaps();

	List<Listener> getListeners();

	default List<Object> getCommands() {
		return new ArrayList<>();
	}

	EventGameLogic start(EventGame game);

	void save();

}
