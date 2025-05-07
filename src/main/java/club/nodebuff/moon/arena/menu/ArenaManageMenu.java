package club.nodebuff.moon.arena.menu;

import lombok.AllArgsConstructor;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.arena.Arena;
import club.nodebuff.moon.arena.impl.StandaloneArena;
import club.nodebuff.moon.arena.runnables.StandalonePasteRunnable;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.arena.ArenaType;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.Constants;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.Menu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArenaManageMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return Moon.get().getMenusConfig().getString("MANAGE.ARENA.TITLE");
	}
   // Moon.get().getMenusConfig().getInteger("MANAGE.ARENA.SIZE");
    @Override
    public int getSize() {
		return 54;
    }

	@Override
	public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
		Profile profile = Profile.getByUuid(player.getUniqueId());

        int i = 10;

		for (int j = 0; j < getSize(); ++j) {
			buttons.put(j, Constants.BLACK_PANE);
		}

		for (Arena arena : Arena.getArenas()) {
            while (i == 17 || i == 18 || i == 27 || i == 36) {
                i++;
            }
            if (arena.getType() != ArenaType.DUPLICATE) {
			    buttons.put(i++, new ArenaButton(arena));
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
			lore.add(" &aLeft-Click to generate duplicate.");
            lore.add(" &aRight-Click to teleport to this arena.");
            if (arena.getType() == ArenaType.STANDALONE) {
                lore.add(" &aMiddle-Click to open duplicates menu.");
            }
            lore.add("");
			return new ItemBuilder(Material.PAPER)
					.name("&b&l" + arena.getName())
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.getByUuid(player.getUniqueId());
            if (clickType == ClickType.LEFT) {
                // Return if the arena is shared or duplicate
                if (arena.getType() == ArenaType.SHARED || arena.getType() == ArenaType.DUPLICATE) {
                    player.sendMessage(CC.translate("&cYou can't paste that type of Arena!"));
                    return;
                }

                // Clone the arena
                new StandalonePasteRunnable(player, arena);

               // Send generate message
               player.sendMessage(CC.translate("&aGenerating duplicate..."));
            } else if (clickType == ClickType.RIGHT) {
                // Teleport player
                player.teleport(arena.getSpawnA());

                // Send teleport message
                player.sendMessage(CC.translate("&fTeleported to arena &b" + arena.getName()));
            } else if (clickType == ClickType.MIDDLE && arena.getType() == ArenaType.STANDALONE) {
                new ArenaDuplicateMenu(arena).openMenu(player);
            }

			// Set closed by menu
			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

			// Force close inventory
			//player.closeInventory();
		}

	}

}
