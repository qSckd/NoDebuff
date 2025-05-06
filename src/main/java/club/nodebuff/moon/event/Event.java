package club.nodebuff.moon.event;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.event.game.EventGame;
import club.nodebuff.moon.event.game.EventGameLogic;
import club.nodebuff.moon.event.impl.sumo.SumoEvent;
import club.nodebuff.moon.event.impl.spleef.SpleefEvent;
import club.nodebuff.moon.event.impl.brackets.BracketsEvent;
import club.nodebuff.moon.event.impl.gulag.GulagEvent;
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
				Moon.get().getServer().getPluginManager().registerEvents(listener, Moon.get());
			}

			for (Object command : event.getCommands()) {
				Moon.get().getHoncho().registerCommand(command);
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
