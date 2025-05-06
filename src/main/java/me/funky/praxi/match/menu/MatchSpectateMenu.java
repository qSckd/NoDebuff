package me.funky.praxi.match.menu;

import me.funky.praxi.Praxi;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.funky.praxi.match.Match;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.match.impl.BasicTeamMatch;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchSpectateMenu extends PaginatedMenu {

    {
        this.setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.translate(Praxi.get().getMenusConfig().getString("MATCH-SPECTATE-MENU.TITLE").replace("{amount}", String.valueOf(Match.getMatches().size())));
    }

    /*@Override
    public int getSize() {
		return Praxi.get().getMenusConfig().getInteger("MATCH-SPECTATE-MENU.SIZE");
    }*/

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new ConcurrentHashMap<>();

        int slot = 0; //10

        for (Match match : Praxi.get().getCache().getMatches()) {
            if(match instanceof BasicTeamMatch) {
                buttons.put(slot, new MatchButton(match));
            }
           slot++;
        }
        return buttons;
    }

    @RequiredArgsConstructor
    public static class MatchButton extends Button {

        private final Match match;

        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lore = Praxi.get().getMenusConfig().getStringList("MATCH-SPECTATE-MENU.LORE");
            lore.replaceAll(s ->
                    CC.translate(s
                            .replace("{arena}", match.getArena().getName())
                            .replace("{duration}", match.getDuration())
                            .replace("{spectators}", String.valueOf(match.getSpectators().size()))
                            .replace("{kit}", match.getKit().getName())
                            .replace("{type}", match.isRanked() ? "Ranked" : "Unranked")
                            .replace("{theme}", "&" + profile.getOptions().theme().getColor().getChar())
                    )
            );

            if (match instanceof BasicTeamMatch) {
                return new ItemBuilder(match.getKit().getDisplayIcon().clone())
                    .name(CC.translate(Praxi.get().getMenusConfig().getString("MATCH-SPECTATE-MENU.NAME")
                                    .replace("{playerA}", String.valueOf(((BasicTeamMatch) match).getParticipantA().getLeader().getPlayer().getName()))
                                    .replace("{playerB}", String.valueOf(((BasicTeamMatch) match).getParticipantB().getLeader().getPlayer().getName()))
                                    .replace("{theme}", "&" + profile.getOptions().theme().getColor().getChar())
                            )
                    )
                    .lore(lore)
                    .build();
            }
            return null;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            player.chat("/spectate " + ((BasicTeamMatch) match).getParticipantA().getLeader().getPlayer().getName());
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
		    return true;
	    }
    }

}