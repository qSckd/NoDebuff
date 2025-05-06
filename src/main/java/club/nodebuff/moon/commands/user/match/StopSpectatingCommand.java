package club.nodebuff.moon.commands.user.match;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("stopspectating|stopspec")
@Description("Stop spectating a match")
public class StopSpectatingCommand extends BaseCommand {

    @Default
	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getState() == ProfileState.FIGHTING && profile.getMatch().getGamePlayer(player).isDead()) {
			profile.getMatch().getGamePlayer(player).setDisconnected(true);
			profile.setState(ProfileState.LOBBY);
			profile.setMatch(null);
		} else if (profile.getState() == ProfileState.SPECTATING) {
			profile.getMatch().removeSpectator(player);
		} else {
			player.sendMessage(CC.RED + "You are not spectating a match.");
		}
	}

}
