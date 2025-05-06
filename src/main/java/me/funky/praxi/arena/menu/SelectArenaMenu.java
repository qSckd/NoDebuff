package me.funky.praxi.arena.menu;

import me.funky.praxi.Praxi;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.Map;

public class SelectArenaMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return Praxi.get().getMenusConfig().getString("ARENA-SELECT-MENU.TITLE");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		return super.getButtons();
	}

}
