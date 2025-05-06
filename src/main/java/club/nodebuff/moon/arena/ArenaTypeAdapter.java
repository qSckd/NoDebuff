package club.nodebuff.moon.arena;

import club.nodebuff.moon.util.command.command.adapter.CommandTypeAdapter;

public class ArenaTypeAdapter implements CommandTypeAdapter {

	@Override
	public <T> T convert(String string, Class<T> type) {
		return type.cast(Arena.getByName(string));
	}

}

