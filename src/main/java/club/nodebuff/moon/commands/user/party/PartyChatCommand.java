package club.nodebuff.moon.commands.user.party;

import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.command.command.CPL;
import club.nodebuff.moon.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "party chat", "p chat" })
public class PartyChatCommand {

	public void execute(Player player, String message) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getParty() != null) {
			profile.getParty().sendChat(player, message);
		}
	}

}
