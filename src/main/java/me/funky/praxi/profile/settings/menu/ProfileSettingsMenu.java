package me.funky.praxi.profile.settings.menu;

import lombok.AllArgsConstructor;
import me.funky.praxi.Praxi;
import me.funky.praxi.divisions.menu.ProfileDivisionsMenu;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.event.Event;
import me.funky.praxi.event.game.EventGame;
import me.funky.praxi.event.game.map.EventGameMap;
import me.funky.praxi.event.game.map.vote.EventGameMapVoteData;
import me.funky.praxi.event.game.menu.EventHostMenu;
import me.funky.praxi.leaderboard.menu.StatsMenu;
import me.funky.praxi.profile.settings.menu.SettingsMenu;
import me.funky.praxi.profile.coinshop.ProfileCosmeticsMenu;
import me.funky.praxi.profile.replay.menu.ReplayMenu;
import me.funky.praxi.profile.history.MatchHistoryMenu;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.Constants;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.TextSplitter;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
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
        return Praxi.get().getMenusConfig().getString("PROFILE-SETTINGS.TITLE");
    }

    @Override
    public int getSize() {
        return Praxi.get().getMenusConfig().getInteger("PROFILE-SETTINGS.SIZE");
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