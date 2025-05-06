package club.nodebuff.moon.commands.user.party;

import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.command.command.CPL;
import club.nodebuff.moon.util.command.command.CommandMeta;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "p leave", "party leave" })
public class PartyLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getParty() == null) {
			player.sendMessage(CC.RED + "You do not have a party.");
			return;
		}

		if (profile.getParty().getLeader().equals(player)) {
			profile.getParty().disband();
		} else {
			profile.getParty().leave(player, false);
		}
	}

}
