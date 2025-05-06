package club.nodebuff.moon.commands.event.user;

import club.nodebuff.moon.event.game.EventGame;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.util.command.command.CPL;
import club.nodebuff.moon.util.command.command.CommandMeta;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "event leave")
public class EventLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getState() == ProfileState.EVENT) {
			EventGame.getActiveGame().getGameLogic().onLeave(player);
		} else {
			player.sendMessage(CC.RED + "You are not in an event.");
		}
	}

}
