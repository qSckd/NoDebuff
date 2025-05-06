package club.nodebuff.moon.match.menu;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.match.impl.BasicTeamMatch;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.pagination.PaginatedMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchSpectateMenu extends PaginatedMenu {

    public MatchSpectateMenu() {
        this.setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        String title = Moon.get().getMenusConfig().getString("MATCH-SPECTATE-MENU.TITLE")
                .replace("{amount}", String.valueOf(Match.getMatches().size()));
        return CC.translate(title);
    }

    @Override
    public int getSize() {
        return Moon.get().getMenusConfig().getInteger("MATCH-SPECTATE-MENU.SIZE");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new ConcurrentHashMap<>();

        int slot = 0;
        for (Match match : Moon.get().getCache().getMatches()) {
            if (match instanceof BasicTeamMatch) {
                buttons.put(slot, new MatchButton(match));
                slot++;
            }
        }
        return buttons;
    }

    @RequiredArgsConstructor
    public static class MatchButton extends Button {

        private final Match match;

        @Override
        public ItemStack getButtonItem(Player player) {

            Profile profile = Profile.getByUuid(player.getUniqueId());

            List<String> lore = Moon.get().getMenusConfig().getStringList("MATCH-SPECTATE-MENU.LORE");
            lore.replaceAll(s -> CC.translate(s
                    .replace("{arena}", match.getArena().getName())
                    .replace("{duration}", match.getDuration())
                    .replace("{spectators}", String.valueOf(match.getSpectators().size()))
                    .replace("{kit}", match.getKit().getName())
                    .replace("{type}", match.isRanked() ? "Ranked" : "Unranked")
                    .replace("{theme}", "&" + profile.getOptions().theme().getColor().getChar())
            ));

            if (match instanceof BasicTeamMatch) {
                Player playerA = ((BasicTeamMatch) match).getParticipantA().getLeader().getPlayer();
                Player playerB = ((BasicTeamMatch) match).getParticipantB().getLeader().getPlayer();

                if (playerA == null || playerB == null) return null;

                return new ItemBuilder(match.getKit().getDisplayIcon().clone())
                        .name(CC.translate(Moon.get().getMenusConfig().getString("MATCH-SPECTATE-MENU.NAME")
                                .replace("{playerA}", playerA.getName())
                                .replace("{playerB}", playerB.getName())
                                .replace("{theme}", "&" + profile.getOptions().theme().getColor().getChar())))
                        .lore(lore)
                        .build();
            }
            return null;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (match instanceof BasicTeamMatch) {
                Player leader = ((BasicTeamMatch) match).getParticipantA().getLeader().getPlayer();
                if (leader != null) {
                    player.chat("/spectate " + leader.getName());
                } else {
                    player.sendMessage(CC.translate("&cEl líder ya no está conectado."));
                }
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }
    }

}