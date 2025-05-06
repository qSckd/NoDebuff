package club.nodebuff.moon.commands.user.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.leaderboard.menu.StatsMenu;
import club.nodebuff.moon.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("stats")
@Description("Display player stats.")
public class StatsCommand extends BaseCommand {

    @Default
    public void open(Player player) {
        new StatsMenu(player).openMenu(player);
    }

    @Default
    @Syntax("<target>")
    @CommandCompletion("@players")
    public void statsTargets(Player player, String targetPlayer) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetPlayer);
        Profile targetProfile = Profile.getByUuid(target.getUniqueId());
        if (targetProfile == null) {
            player.sendMessage(CC.translate("&cThis player doesn't exist."));
            return;
        }
		new StatsMenu(target).openMenu(player);
    }
}