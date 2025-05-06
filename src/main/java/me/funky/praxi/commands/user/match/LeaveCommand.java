package me.funky.praxi.commands.user.match;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.ProfileState;
import me.funky.praxi.util.CC;
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