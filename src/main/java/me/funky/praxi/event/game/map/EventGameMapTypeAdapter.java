package club.nodebuff.moon.event.game.map;


import club.nodebuff.moon.util.command.command.adapter.CommandTypeAdapter;

public class EventGameMapTypeAdapter implements CommandTypeAdapter {

	@Override
	public <T> T convert(String string, Class<T> type) {
		return type.cast(EventGameMap.getByName(string));
	}

}

