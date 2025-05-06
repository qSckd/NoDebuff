package club.nodebuff.moon.party.menu;

import club.nodebuff.moon.party.PartyPrivacy;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.Constants;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Author: Trakser, Zatrex
   Project: Shadow
   Date: 2024/5/29
 */

public class PartyManageMenu extends Menu {
    {
        setUpdateAfterClick(true);
        setAutoUpdate(true);

    }
    @Override
    public String getTitle(Player player) {
        return "&8Party Settings";
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
        buttons.put(11, new PartyStatusButton());
        buttons.put(10, new PartySizeButton());
        buttons.put(12, new PartyAnnounceButton());
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }


    private static class PartyStatusButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(player.getUniqueId());
            lore.add(CC.translate("&fMake your party open or closed"));
            lore.add(CC.translate("&fCurrent Privacy: &b" + profile.getParty().getPrivacy()));
            lore.add("");
            lore.add(CC.translate("&aClick here to change the status of your party"));
            return new ItemBuilder(Material.NETHER_STAR).name(CC.translate("&b&lParty Privacy")).lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getParty().getPrivacy() == PartyPrivacy.CLOSED) {
                profile.getParty().setPrivacy(PartyPrivacy.OPEN);
                new PartyManageMenu().openMenu(player);
                player.updateInventory();
            } else {
                profile.getParty().setPrivacy(PartyPrivacy.CLOSED);
                new PartyManageMenu().openMenu(player);
                player.updateInventory();
            }

        }

    }
    private static class PartySizeButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fHow many players would you like."));
            lore.add(CC.translate("&fto be able to join your party?"));
            lore.add(CC.translate("&r"));
            lore.add(CC.translate("&fCurrent limit: &b" + profile.getParty().getLimit()));
            lore.add(CC.translate("&r"));
            if (!player.hasPermission("moon.donator")) {
               lore.add(CC.translate("&aNo Permission."));
			} else if (player.hasPermission("moon.donator")) {
                lore.add(CC.translate("&aLeft-click to add 1."));
                lore.add(CC.translate("&aRight-click to remove 1."));
            }
            return new ItemBuilder(Material.BOOK).name(CC.translate("&b&lParty Limit")).lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (!player.hasPermission("moon.donator")) {
                player.sendMessage(CC.RED + "You need a Donator Rank for this");
                return;
            }
            int limit = profile.getParty().getLimit();
            if (clickType == ClickType.LEFT) {
                if (profile.getParty().getLimit() < 100) {
                    profile.getParty().setLimit(limit + 1);
                    new PartyManageMenu().openMenu(player);
                    player.updateInventory();
                } else {
                    player.sendMessage(CC.translate("&cThe maximum players limit is 100"));
                }
            } if (clickType == ClickType.RIGHT) {
                if (profile.getParty().getLimit() > 2) {
                    profile.getParty().setLimit(limit - 1);
                    new PartyManageMenu().openMenu(player);
                    player.updateInventory();
                } else {
                    player.sendMessage(CC.translate("&cThe minimum players limit is 2"));
                }
            }
        }
    }
    private static class PartyAnnounceButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fAnnounce your party in the global chat !"));
            lore.add("");
            lore.add(CC.translate("&aClick here to announce your party!"));
            return new ItemBuilder(Material.ARROW).name(CC.translate("&b&lParty Announce")).lore(lore).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (!player.hasPermission("moon.donator")) {
                player.sendMessage(CC.RED + "You need a Donator Rank for this");
                return;
            }
            if (profile.getParty().getPrivacy() == PartyPrivacy.CLOSED) {
                player.sendMessage(CC.translate("&bYour party is closed"));
                return;
            }
            Bukkit.broadcastMessage(CC.translate("&7---&f*&7----------------------&f*&7---"));
            Bukkit.broadcastMessage(CC.translate("&f&l" + player.getName() + "&b's Party is public to everyone."));
            Bukkit.broadcastMessage(CC.translate("&bFeel free to join"));
            Bukkit.broadcastMessage(CC.translate("&7---&f*&7----------------------&f*&7---"));
            new PartyManageMenu().openMenu(player);
            player.updateInventory();
        }
    }
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}
