package club.nodebuff.moon.profile.coinshop;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.profile.coinshop.submenu.killeffects.SpecialEffectsPurchaseMenu;
import club.nodebuff.moon.profile.coinshop.submenu.killmessages.KillMessagesPurchaseMenu;
import club.nodebuff.moon.profile.coinshop.submenu.trails.ProjectileTrailsPurchaseMenu;
import club.nodebuff.moon.util.Constants;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.Menu;
import club.nodebuff.moon.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinShopMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&8Coin Shop";
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        for (int k = 0; k < 27; k++) {
            buttons.put(k, Constants.BLACK_PANE);
        }
        buttons.put(10, new SpecialEffectsPurchaseButton());
        buttons.put(12, new KillMessagesPurchaseButton());
        buttons.put(14, new ProjectileTrailsPurchaseButton());
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }

    private static class SpecialEffectsPurchaseButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fKill Effects make your matches look better!"));
            lore.add(CC.translate("&fThey show up when someone dies in a match."));
            lore.add("");
            lore.add(CC.translate("&aClick here to purchase some kill effects!"));
            return new ItemBuilder(Material.BEACON).name(CC.translate("&cKill Effects")).lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new SpecialEffectsPurchaseMenu().openMenu(player);
        }
    }

    private static class KillMessagesPurchaseButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fKill Messages annoy your opponent!"));
            lore.add(CC.translate("&fThey show up when a match ends."));
            lore.add("");
            lore.add(CC.translate("&aClick here to purchase some kill messages!"));
            return new ItemBuilder(Material.CHEST).name(CC.translate("&cKill Messages")).lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new KillMessagesPurchaseMenu().openMenu(player);
        }
    }
    private static class ProjectileTrailsPurchaseButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&f"));
            lore.add(CC.translate("&f"));
            lore.add("");
            lore.add(CC.translate("&aClick here to purchase some projectile trails!"));
            return new ItemBuilder(Material.CHEST).name(CC.translate("&cProjectile Trails")).lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new ProjectileTrailsPurchaseMenu().openMenu(player);
        }
    }
}
