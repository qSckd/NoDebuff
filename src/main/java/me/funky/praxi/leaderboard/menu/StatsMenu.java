package me.funky.praxi.leaderboard.menu;

import me.funky.praxi.Praxi;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.divisions.ProfileDivision;
import me.funky.praxi.util.Constants;
import me.funky.praxi.util.SkullCreator;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.ProgressBar;
import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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

public class StatsMenu extends Menu {

    private static OfflinePlayer target;

    @Override
    public String getTitle(Player player) {
        return Praxi.get().getMenusConfig().getString("STATS.TITLE").replace("{player}", target.getName());
    }

    @Override
    public int getSize(){
	    return Praxi.get().getMenusConfig().getInteger("STATS.SIZE");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (int i = 0; i < getSize(); i++) {
            buttons.put(i, Constants.BLACK_PANE);
        }
        final int[] i = {10};
        Kit.getKits().forEach(kit -> {
            if (!kit.isEnabled()) return;
            while (i[0] == 17 || i[0] == 18 || i[0] == 26 || i[0] == 27 || i[0] == 36 || i[0] == 37) i[0]++;
            buttons.put(i[0]++, new KitStatsButton(kit));
            buttons.put(4, new GlobalStatsButton());
        });

        return buttons;
    }

    @ConstructorProperties ({"target"})
    public StatsMenu(OfflinePlayer target) {
        this.target = target;
    }

    @AllArgsConstructor
    private class KitStatsButton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());
            String elo = kit.getGameRules().isRanked() ? Integer.toString(profile.getKitData().get(kit).getElo()) : "N/A";
            lore.add(CC.MENU_BAR);
            lore.add("&8 • " + "&bELO: &f" + elo);
            lore.add("&8 • " + "&bWins: &f" + profile.getKitData().get(kit).getWon());
            lore.add("&8 • " + "&bKills: &f" + profile.getKitData().get(kit).getKills());
            lore.add("&8 • " + "&bWinstreak: &f" + profile.getKitData().get(kit).getWinstreak());
            lore.add("&8 • " + "&bLosses: &f" + profile.getKitData().get(kit).getLost());
            lore.add(CC.MENU_BAR);

            return new ItemBuilder(kit.getDisplayIcon())
                    .name("&b&l" + kit.getName() + " &7⏐ &fStats")
                    .lore(lore)
                    .build();
        }

    }

    @AllArgsConstructor
    private static class GlobalStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());
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
            return new ItemBuilder(SkullCreator.itemFromUuid(target.getUniqueId()))
                    .name("&b&l" + target.getName() + " &7&l⏐ &fGlobal Statistics")
                    .lore(lore)
                    .build();
        }
    }

}