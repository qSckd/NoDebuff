package me.funky.praxi.leaderboard.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.google.common.collect.Lists;
import me.funky.praxi.Praxi;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.leaderboard.LeaderboardCache;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.divisions.ProfileDivision;
import me.funky.praxi.util.Constants;
import me.funky.praxi.util.SkullCreator;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.ProgressBar;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Author: Zatrex
   Project: Shadow
   Date: 2024/5/15
 */

@Getter
@AllArgsConstructor
public class LeaderboardsMenu extends Menu {

    private LeaderboardsMode mode;

    @Override
    public String getTitle(final Player player) {
        return "&8Leaderboards";
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(49, new StatsButton());
        buttons.put(47, new SwitchLeaderboardsModeButtonWins());
        buttons.put(45, new SwitchLeaderboardsModeButtonElo());
        buttons.put(51, new SwitchLeaderboardsModeButtonWinStreak());
        int y = 3;
        int x = 1;
        for (Kit kit : Kit.getKits()) {
            if (!kit.isEnabled()) continue;
            //buttons.put(getSlot(x++, y), new KitLeaderboardsButton(kit, mode));
            buttons.put(kit.getSlot(), new KitLeaderboardsButton(kit, mode));
            if (x == 8) {
                y++;
                x = 1;
            }
        }
        for (int i = 0; i < 54; i++) {
            buttons.putIfAbsent(i, Constants.BLACK_PANE);
        }
        return buttons;
    }

    private static class KitLeaderboardsButton extends Button
    {
        private final Kit kit;
        private LeaderboardsMode mode;

        @Override
        public ItemStack getButtonItem(final Player player) {
            List<String> lore = Lists.newArrayList();
            lore.add(CC.MENU_BAR);
            switch (mode) {
                case ELO: lore.addAll(LeaderboardCache.getCachedLores().get(kit)); 
				  break;
                case WINS: lore.addAll(LeaderboardCache.getCachedWinsLores().get(kit)); 
				  break;
                case WINSTREAK: lore.addAll(LeaderboardCache.getCachedWinStreakLores().get(kit)); 
				  break;
            }
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(this.kit.getDisplayIcon()).name("&b&l" + this.kit.getName() + " &7&l⏐ &fTop 10").lore(lore).clearFlags().build();
        }

        @ConstructorProperties({ "kit", "mode" })
        public KitLeaderboardsButton(final Kit kit, final LeaderboardsMode mode) {
            this.kit = kit;
            this.mode = mode;
        }
    }

    @AllArgsConstructor
    private class SwitchLeaderboardsModeButtonElo extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&aClick to open Elo Leaderboards");
            if (mode == LeaderboardsMode.ELO) {
                return new ItemBuilder(Material.INK_SACK).durability(10).name("&b&lElo Leaderboards").lore(lore).build();
            } else {
                return new ItemBuilder(Material.INK_SACK).durability(8).name("&b&lElo Leaderboards").lore(lore).build();
            }
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
                new LeaderboardsMenu(LeaderboardsMode.ELO).openMenu(player);
        }
    }

    @AllArgsConstructor
    private class SwitchLeaderboardsModeButtonWinStreak extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&aClick to open WinStreak Leaderboards");
            if (mode == LeaderboardsMode.WINSTREAK) {
                return new ItemBuilder(Material.INK_SACK).durability(10).name("&b&lWinStreak Leaderboards").lore(lore).build();
            } else {
                return new ItemBuilder(Material.INK_SACK).durability(8).name("&b&lWinStreak Leaderboards").lore(lore).build();
            }
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
                new LeaderboardsMenu(LeaderboardsMode.WINSTREAK).openMenu(player);
        }
    }

    @AllArgsConstructor
    private class SwitchLeaderboardsModeButtonWins extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&aClick to open Wins Leaderboards");
            if (mode == LeaderboardsMode.WINS) {
                return new ItemBuilder(Material.INK_SACK).durability(10).name("&b&lWins Leaderboards").lore(lore).build();
            } else {
                return new ItemBuilder(Material.INK_SACK).durability(8).name("&b&lWins Leaderboards").lore(lore).build();
            }
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
                new LeaderboardsMenu(LeaderboardsMode.WINS).openMenu(player);
        }
    }

    @AllArgsConstructor
    private class StatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(player.getUniqueId());
            ProfileDivision expDivision = Praxi.get().getDivisionsManager().getNextDivisionByXP(profile.getExperience());
            ProfileDivision eloDivision = Praxi.get().getDivisionsManager().getNextDivisionByELO(profile.getExperience());

            lore.add(CC.MENU_BAR);
                    lore.add("&f&lElo: &b" + profile.getGlobalElo());
					lore.add("&f&lExperience: &b" + profile.getExperience());
					lore.add("&f&lDivision: &b" + profile.getDivision().getDisplayName());
                    if (Praxi.get().getDivisionsManager().isXPBased()) {
                        lore.add(" " + ProgressBar.getBar(profile.getExperience(), expDivision.getExperience()));
                    } else {
                        lore.add(" " + ProgressBar.getBar(profile.getExperience(), eloDivision.getExperience()));
                    }
					lore.add("&f&lWins: &b" + profile.getWins());
					lore.add("&f&lKills: &b" + profile.getKills());
					lore.add("&f&lLosses: &b" + profile.getLoses());
					//lore.add("&f&lDeaths: &b" + profile.getDeaths());
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(SkullCreator.itemFromUuid(player.getUniqueId()))
                    .name("&b&l" + player.getName() + " &7&l⏐ &fGlobal Statistics")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.chat("/stats");
        }
    }
}