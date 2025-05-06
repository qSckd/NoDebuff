package me.funky.praxi.commands.user.party;

import me.funky.praxi.Locale;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.util.command.command.CPL;
import me.funky.praxi.util.command.command.CommandMeta;
import me.funky.praxi.util.CC;
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
