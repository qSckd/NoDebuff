package club.nodebuff.moon.commands.user.general.leaderboards;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.leaderboard.menu.LeaderboardsMenu;
import club.nodebuff.moon.leaderboard.menu.LeaderboardsMode;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("leaderboards|leaderboard")
@Description("Open leaderboards menu.")
public class LeaderboardsCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getState() != ProfileState.LOBBY && !player.isOp()) {
            player.sendMessage(CC.translate("&cYou can't use this command in match."));
            return;
        }
        new LeaderboardsMenu(LeaderboardsMode.ELO).openMenu(player);
    }
}
