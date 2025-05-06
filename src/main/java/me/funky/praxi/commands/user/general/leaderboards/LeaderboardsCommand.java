package me.funky.praxi.commands.user.general.leaderboards;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.leaderboard.menu.LeaderboardsMenu;
import me.funky.praxi.leaderboard.menu.LeaderboardsMode;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.ProfileState;
import me.funky.praxi.util.CC;
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
