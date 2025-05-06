package club.nodebuff.moon.profile.settings.menu;

import lombok.AllArgsConstructor;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.divisions.menu.ProfileDivisionsMenu;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.event.Event;
import club.nodebuff.moon.event.game.EventGame;
import club.nodebuff.moon.event.game.map.EventGameMap;
import club.nodebuff.moon.event.game.map.vote.EventGameMapVoteData;
import club.nodebuff.moon.event.game.menu.EventHostMenu;
import club.nodebuff.moon.leaderboard.menu.StatsMenu;
import club.nodebuff.moon.profile.settings.menu.SettingsMenu;
import club.nodebuff.moon.profile.coinshop.ProfileCosmeticsMenu;
import club.nodebuff.moon.profile.replay.menu.ReplayMenu;
import club.nodebuff.moon.profile.history.MatchHistoryMenu;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.Constants;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.TextSplitter;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileSettingsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return Moon.get().getMenusConfig().getString("PROFILE-SETTINGS.TITLE");
    }

    @Override
    public int getSize() {
        return Moon.get().getMenusConfig().getInteger("PROFILE-SETTINGS.SIZE");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 10;
        for (int j = 0; j < getSize(); ++j) {
			buttons.put(j, Constants.BLACK_PANE);
		}
        while (i == 17 || i == 18 || i == 27 || i == 36) {
             i++;
        }

        buttons.put(15, new HistoryButton());
        buttons.put(14, new DivisionsButton());
        buttons.put(13, new StatsButton());
		buttons.put(12, new CosmeticsButton());
        buttons.put(11, new SettingButton());
        buttons.put(10, new EventHostButton());

        return buttons;
    }

    @AllArgsConstructor
    private class SettingButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fView or change your"));
            lore.add(CC.translate("&fpersonal profile settings."));
            lore.add(CC.translate(""));
            lore.add(CC.translate("&aClick to open"));

            return new ItemBuilder(Material.REDSTONE_COMPARATOR)
                    .name(CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + "Settings Menu")
                    .lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
			new SettingsMenu().openMenu(player);
        }
    }

    @AllArgsConstructor
    private class EventHostButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fHost Events."));
            lore.add(CC.translate(""));
            lore.add(CC.translate("&aClick to open"));

            return new ItemBuilder(Material.PAPER)
                    .name(CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + "Events Menu")
                    .lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new EventHostMenu().openMenu(player);
        }
    }

	@AllArgsConstructor
    private class CosmeticsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fOpen Cosmetics Menu"));
            lore.add(CC.translate("&fto edit and buy stuffs."));
            lore.add(CC.translate(""));
            lore.add(CC.translate("&aClick to open"));

            return new ItemBuilder(Material.FIREWORK)
                    .name(CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + "Cosmetics")
                    .lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
			new ProfileCosmeticsMenu().openMenu(player);
        }
    }

    @AllArgsConstructor
    private class StatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fView your personal"));
            lore.add(CC.translate("&fstats."));
            lore.add(CC.translate(""));
            lore.add(CC.translate("&aClick to open"));

            return new ItemBuilder(Material.EMERALD)
                    .name(CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + "Statistics")
                    .lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new StatsMenu(player).openMenu(player);
        }
    }

    @AllArgsConstructor
    private class DivisionsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fView your personal"));
            lore.add(CC.translate("&fdivisions."));
            lore.add(CC.translate(""));
            lore.add(CC.translate("&aClick to open"));

            return new ItemBuilder(Material.ITEM_FRAME)
                    .name(CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + "Divisions Menu")
                    .lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
			new ProfileDivisionsMenu(profile).openMenu(player);
        }
    }

    @AllArgsConstructor
    private class HistoryButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&fShow your"));
            lore.add(CC.translate("&fmatches history."));
            lore.add(CC.translate(""));
            lore.add(CC.translate("&aClick to open"));

            return new ItemBuilder(Material.REDSTONE_COMPARATOR)
                    .name(CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + "Match History")
                    .lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
			new MatchHistoryMenu(profile).openMenu(player);
        }
    }
}