package club.nodebuff.moon.arena.menu;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.Map;

public class SelectArenaMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return Moon.get().getMenusConfig().getString("ARENA-SELECT-MENU.TITLE");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		return super.getButtons();
	}

}
