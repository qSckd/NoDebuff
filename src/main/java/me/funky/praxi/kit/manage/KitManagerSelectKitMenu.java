package me.funky.praxi.kit.manage;

import lombok.AllArgsConstructor;
import me.funky.praxi.Praxi;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.kit.menu.KitManagementMenu;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.util.Constants;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import me.funky.praxi.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitManagerSelectKitMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&8Kits Management";
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 10;
        for (int j = 0; j < getSize(); ++j) {
			buttons.put(j, Constants.BLACK_PANE);
		}
        for (Kit kit : Kit.getKits()) {
            buttons.put(kit.getSlot(), new KitDisplayButton(kit));
        }
        return buttons;
    }

    @AllArgsConstructor
    private class KitDisplayButton extends Button {

        private Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(kit.getDisplayIcon())
                    .name("&b&l" + kit.getName())
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            new KitManagerMenu(kit).openMenu(player);
        }
    }
}
