package me.funky.praxi.duel.menu;

import lombok.AllArgsConstructor;
import me.funky.praxi.Praxi;
import me.funky.praxi.arena.Arena;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.arena.ArenaType;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DuelSelectArenaMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return Praxi.get().getMenusConfig().getString("DUEL.SELECT-ARENA-MENU.TITLE");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		Map<Integer, Button> buttons = new HashMap<>();

		for (Arena arena : Arena.getArenas()) {
			if (!arena.isSetup()) {
				continue;
			}

			if (!arena.getKits().contains(profile.getDuelProcedure().getKit().getName())) {
				continue;
			}

			if (!profile.getDuelProcedure().getKit().getGameRules().isBuild()) {
				if (arena.getType() == ArenaType.SHARED) {
					continue;
				}

				if (arena.getType() != ArenaType.STANDALONE || arena.getType() != ArenaType.DUPLICATE) {
					continue;
				}

				if (arena.isActive()) {
					continue;
				}
			} else {
                if (arena.getType() != ArenaType.SHARED) {
					continue;
				}

				if (arena.getType() == ArenaType.STANDALONE || arena.getType() == ArenaType.DUPLICATE) {
					continue;
				}
            }

			buttons.put(buttons.size(), new SelectArenaButton(arena));
		}

		return buttons;
	}

	@Override
	public void onClose(Player player) {
		if (!isClosedByMenu()) {
			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.setDuelProcedure(null);
		}
	}

	@AllArgsConstructor
	private class SelectArenaButton extends Button {

		private Arena arena;

		@Override
		public ItemStack getButtonItem(Player player) {
			List<String> lore = new ArrayList<>();
			lore.add("");
			lore.add(" &aClick to select this arena.");
			return new ItemBuilder(Material.PAPER)
					.name("&b&l" + arena.getName())
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.getByUuid(player.getUniqueId());

			// Update and request the procedure
			profile.getDuelProcedure().setArena(this.arena);
			profile.getDuelProcedure().send();

			// Set closed by menu
			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

			// Force close inventory
			player.closeInventory();
		}

	}

}
