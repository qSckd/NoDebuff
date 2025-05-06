package club.nodebuff.moon.kit;

import club.nodebuff.moon.util.command.command.adapter.CommandTypeAdapter;

public class KitTypeAdapter implements CommandTypeAdapter {

	@Override
	public <T> T convert(String string, Class<T> type) {
		return type.cast(Kit.getByName(string));
	}

}
