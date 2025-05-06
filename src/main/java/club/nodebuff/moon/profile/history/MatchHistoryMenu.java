package club.nodebuff.moon.profile.history;

import lombok.RequiredArgsConstructor;
import me.jumper251.replay.api.ReplayAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.match.MatchInfo;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.TimeUtil;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MatchHistoryMenu extends PaginatedMenu {

    private final Profile profile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&8Match History";
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 45;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<MatchInfo> matches = profile.getMatchHistory();
        int size = matches.size();

        for (int i = 0; i < size; i++) {
            MatchInfo matchInfo = matches.get(size - i - 1);
            int matchNumber = size - i;
            buttons.put(buttons.size(), new MatchButton(matchInfo, profile, matchNumber));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private static class MatchButton extends Button {

        private final MatchInfo matchInfo;
        private final Profile profile;
        private final int matchNumber;

        @Override
        public ItemStack getButtonItem(Player player) {
            boolean isWinner = player.getName().equalsIgnoreCase(matchInfo.getWinningParticipant());
            String result = isWinner ? "&a(Won)" : "&c(Lost)";

            ItemStack icon = (matchInfo.getKit() != null) ? matchInfo.getKit().getDisplayIcon() : new ItemStack(Material.BOOK);

            return new ItemBuilder(icon)
                    .name("&b" + matchInfo.getWinningParticipant() + " &fvs. &b" + matchInfo.getLosingParticipant() + " " + result)
                    .lore("")
                    .lore(" &7■ &fType: &b" + matchInfo.getType())
                    .lore(" &7■ &fDate: &b" + matchInfo.getDate())
                    .lore(" &7■ &fKit: &b" + (matchInfo.getKit() != null ? matchInfo.getKit().getName() : "Unknown"))
                    .lore("")
                    .lore(" &aClick to watch replay!")
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!Moon.get().isReplay()) return;
            String replayId = profile.getUuid().toString() + "-" + matchInfo.getUuid();
            player.performCommand("replay play " + replayId);
        }
    }
}
