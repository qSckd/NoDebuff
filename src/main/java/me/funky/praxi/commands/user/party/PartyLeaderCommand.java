package club.nodebuff.moon.commands.user.party;

import club.nodebuff.moon.Locale;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.command.command.CPL;
import club.nodebuff.moon.util.command.command.CommandMeta;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "p leader", "party leader" })
public class PartyLeaderCommand {

	public void execute(Player player, Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
			player.sendMessage(CC.RED + "You do not have a party.");
			return;
		}
        if (!profile.getParty().getLeader().equals(player)) {
			player.sendMessage(CC.RED + "You are not the leader of your party.");
			return;
        }
        if (profile.getParty().getLeader().equals(target)) {
			player.sendMessage(CC.RED + "You are already the leader of your party.");
			return;
        }
        profile.getParty().setLeader(target);
	}
}
