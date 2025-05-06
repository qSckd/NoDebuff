package club.nodebuff.moon.match.menu;

import club.nodebuff.moon.Moon;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.match.impl.BasicTeamMatch;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.pagination.PaginatedMenu;

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
        return CC.translate(Moon.get().getMenusConfig().getString("MATCH-SPECTATE-MENU.TITLE").replace("{amount}", String.valueOf(Match.getMatches().size())));
    }

    /*@Override
    public int getSize() {
		return Moon.get().getMenusConfig().getInteger("MATCH-SPECTATE-MENU.SIZE");
    }*/

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new ConcurrentHashMap<>();

        int slot = 0; //10

        for (Match match : Moon.get().getCache().getMatches()) {
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
            List<String> lore = Moon.get().getMenusConfig().getStringList("MATCH-SPECTATE-MENU.LORE");
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
                    .name(CC.translate(Moon.get().getMenusConfig().getString("MATCH-SPECTATE-MENU.NAME")
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