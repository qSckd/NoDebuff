package club.nodebuff.moon.commands.user.match;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("leave|leavematch")
@Description("Leave match.")
public class LeaveCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getState() != ProfileState.FIGHTING) {
            player.sendMessage(CC.translate("&cYou can use this command in match only."));
        } else {
            profile.getMatch().onDeath(player);
            return;
            //player.sendMessage(CC.translate("&c&lIn Development."));
        }
    }
}