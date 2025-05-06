package me.funky.praxi.arena.menu;

import lombok.AllArgsConstructor;
import me.funky.praxi.Praxi;
import me.funky.praxi.arena.Arena;
import me.funky.praxi.arena.impl.StandaloneArena;
import me.funky.praxi.arena.runnables.StandalonePasteRunnable;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.arena.ArenaType;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.Constants;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ArenaDuplicateMenu extends Menu {

    private Arena arena;

	@Override
	public String getTitle(Player player) {
		return Praxi.get().getMenusConfig().getString("MANAGE.DUPLICATE.TITLE");
	}

    @Override
    public int getSize() {
		return Praxi.get().getMenusConfig().getInteger("MANAGE.DUPLICATE.SIZE");
    }

	@Override
	public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
		Profile profile = Profile.getByUuid(player.getUniqueId());

        int i = 10;

		for (int j = 0; j < getSize(); ++j) {
			buttons.put(j, Constants.BLACK_PANE);
		}

		for (Arena arenas : ((StandaloneArena) arena).getDuplicatesForArena(arena)) {
            while (i == 17 || i == 18 || i == 27 || i == 36) {
                i++;
            }
            if (arenas.getType() == ArenaType.DUPLICATE) {
			    buttons.put(i++, new ArenaButton(arenas));
            }
		}

		return buttons;
	}

	@AllArgsConstructor
	private class ArenaButton extends Button {

		private Arena arena;

		@Override
		public ItemStack getButtonItem(Player player) {
			List<String> lore = new ArrayList<>();
			lore.add("");
            lore.add("&fSetup: " + (arena.isSetup() ? "&aTrue" : "&cFalse"));
            lore.add("&fActive: " + (arena.isActive() ? "&aTrue" : "&cFalse"));
            lore.add("&fType: &b" + arena.getType().name());
            lore.add("&fKits: &b" + StringUtils.join(arena.getKits(), ", "));
            lore.add("");
            lore.add(" &aClick to teleport to this duplicate.");
			return new ItemBuilder(Material.PAPER)
					.name("&b&l" + arena.getDisplayName())
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.getByUuid(player.getUniqueId());
            // Teleport player
            player.teleport(arena.getSpawnA());

            // Send teleport message
            player.sendMessage(CC.GREEN + "Teleported to arena " + arena.getName());

			player.closeInventory();
        }

	}

}
