package me.funky.praxi.event;

import me.funky.praxi.event.impl.sumo.SumoEvent;
import me.funky.praxi.event.impl.spleef.SpleefEvent;
import me.funky.praxi.event.impl.brackets.BracketsEvent;
import me.funky.praxi.event.impl.gulag.GulagEvent;
import me.funky.praxi.util.command.command.adapter.CommandTypeAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventTypeAdapter implements CommandTypeAdapter {

	private final static Map<String, Class<? extends Event>> map;

	static {
		map = new HashMap<>();
		map.put("sumo", SumoEvent.class);
		map.put("spleef", SpleefEvent.class);
		map.put("brackets", BracketsEvent.class);
		map.put("gulag", GulagEvent.class);
	}

	@Override
	public <T> T convert(String string, Class<T> type) {
		return type.cast(Event.getEvent(map.get(string.toLowerCase())));
	}

	@Override
	public <T> List<String> tabComplete(String string, Class<T> type) {
		return Arrays.asList("Sumo", "Brackets", "Spleef", "Gulag");
	}

}
