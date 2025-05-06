package club.nodebuff.moon.commands.event.vote;

import club.nodebuff.moon.event.game.EventGame;
import club.nodebuff.moon.event.game.map.EventGameMap;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.util.command.command.CPL;
import club.nodebuff.moon.util.command.command.CommandMeta;
import club.nodebuff.moon.util.Cooldown;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "event map vote")
public class EventMapVoteCommand {

	public void execute(Player player, @CPL("map") EventGameMap gameMap) {
		if (gameMap == null) {
			player.sendMessage(ChatColor.RED + "You cannot vote for a map that doesn't exist!");
			return;
		}

		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getState() == ProfileState.EVENT && EventGame.getActiveGame() != null) {
			if (profile.getVoteCooldown().hasExpired()) {
				profile.setVoteCooldown(new Cooldown(5000));
				EventGame.getActiveGame().getGameLogic().onVote(player, gameMap);
			} else {
				player.sendMessage(ChatColor.RED + "You can vote in another " +
						profile.getVoteCooldown().getTimeLeft() + ".");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You are not in an event.");
		}
	}

}
