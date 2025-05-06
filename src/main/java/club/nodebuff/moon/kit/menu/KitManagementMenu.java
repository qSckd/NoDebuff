package club.nodebuff.moon.kit.menu;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.kit.KitLoadout;
import club.nodebuff.moon.profile.Profile;

import lombok.AllArgsConstructor;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.Menu;
import club.nodebuff.moon.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitManagementMenu extends Menu {

    private static final Button PLACEHOLDER = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " ");
    private static final int INVENTORY_SIZE = 27;
    private final Kit kit;

    public KitManagementMenu(Kit kit) {
        this.kit = kit;
        setPlaceholder(true);
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return "&8Viewing " + kit.getName() + " kits";
    }

    @Override
    public int getSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        KitLoadout[] kitLoadouts = profile.getKitData().get(kit).getLoadouts();

        if (kitLoadouts == null) return buttons;

        for (int i = 0; i < 4; i++) {
            int pos = 10 + i * 2;
            if (pos >= INVENTORY_SIZE) break;

            KitLoadout kitLoadout = kitLoadouts[i];
            buttons.put(pos, kitLoadout == null ? new CreateKitButton(i) : new LoadKitButton(i, kitLoadout));
        }

        buttons.put(26, new BackButton(new KitEditorSelectKitMenu()));
        return buttons;
    }

    @Override
    public void onClose(Player player) {
        if (!isClosedByMenu()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.getKitEditorData().setSelectedKit(null);
        }
    }

    @AllArgsConstructor
    private static class CreateKitButton extends Button {

        private final int index;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BOOK)
                    .name(Moon.get().getMenusConfig().getString("KIT-EDITOR.MANAGEMENT.BUTTONS.CREATE-BUTTON"))
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            Kit kit = profile.getKitEditorData().getSelectedKit();

            if (kit == null) {
                player.closeInventory();
                return;
            }

            KitLoadout kitLoadout = new KitLoadout("Kit " + (index + 1));

            if (kit.getKitLoadout() != null) {
                if (kit.getKitLoadout().getArmor() != null)
                    kitLoadout.setArmor(kit.getKitLoadout().getArmor());

                if (kit.getKitLoadout().getContents() != null)
                    kitLoadout.setContents(kit.getKitLoadout().getContents());
            }

            profile.getKitData().get(kit).replaceKit(index, kitLoadout);
            profile.getKitEditorData().setSelectedKitLoadout(kitLoadout);
            new KitEditorMenu(index).openMenu(player);
        }
    }

    @AllArgsConstructor
    private class LoadKitButton extends Button {

        private final int index;
        private final KitLoadout kitLoadout;

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            String color = "&" + profile.getOptions().theme().getColor().getChar();

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(color + " ┃ &fName: " + color + kitLoadout.getCustomName());
            lore.add(color + " ┃ &fHeals: " + color + kit.countHeals(kitLoadout));
            lore.add(color + " ┃ &fDebuffs: " + color + kit.countDebuffs(kitLoadout));
            lore.add("");
            lore.add("&aClick to edit.");

            return new ItemBuilder(Material.ENCHANTED_BOOK)
                    .name(Moon.get().getMenusConfig().getString("KIT-EDITOR.MANAGEMENT.BUTTONS.LOAD-BUTTON.NAME"))
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            Kit selectedKit = profile.getKitEditorData().getSelectedKit();
            if (selectedKit == null) {
                player.closeInventory();
                return;
            }

            KitLoadout kit = profile.getKitData().get(selectedKit).getLoadout(index);

            if (kit == null) {
                kit = new KitLoadout("Kit " + (index + 1));
                if (selectedKit.getKitLoadout() != null) {
                    if (selectedKit.getKitLoadout().getArmor() != null)
                        kit.setArmor(selectedKit.getKitLoadout().getArmor());

                    if (selectedKit.getKitLoadout().getContents() != null)
                        kit.setContents(selectedKit.getKitLoadout().getContents());
                }
                profile.getKitData().get(selectedKit).replaceKit(index, kit);
            }

            profile.getKitEditorData().setSelectedKitLoadout(kit);
            new KitEditorMenu(index).openMenu(player);
        }
    }
}
