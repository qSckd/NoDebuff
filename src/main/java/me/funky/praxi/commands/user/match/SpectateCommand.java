package club.nodebuff.moon.commands.user.match;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.match.menu.MatchSpectateMenu;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("spectate|spec|watchplayer|watchmatch|specmatch|matchspec")
@Description("Spectate a player.")
public class SpectateCommand extends BaseCommand {

    @Default
    @Syntax("<target>")
	public void execute(Player player, String targe) {
        Player target = Bukkit.getPlayer(targe);
        
		if (player.hasMetadata("frozen")) {
			player.sendMessage(CC.RED + "You cannot spectate while frozen.");
			return;
		}

		if (target == null) {
			player.sendMessage(CC.RED + "A player with that name could not be found.");
			return;
		}

		Profile playerProfile = Profile.getByUuid(player.getUniqueId());

		if (playerProfile.isBusy()) {
			player.sendMessage(CC.RED + "You must be in the lobby and not queueing to spectate.");
			return;
		}

		if (playerProfile.getParty() != null) {
			player.sendMessage(CC.RED + "You must leave your party to spectate a match.");
			return;
		}

		Profile targetProfile = Profile.getByUuid(target.getUniqueId());

		if (targetProfile == null || targetProfile.getState() != ProfileState.FIGHTING) {
			player.sendMessage(CC.RED + "That player is not in a match.");
			return;
		}

		if (!targetProfile.getOptions().allowSpectators()) {
			player.sendMessage(CC.RED + "That player is not allowing spectators.");
			return;
		}

		targetProfile.getMatch().addSpectator(player, target);
	}

    @Subcommand("menu")
	public void menu(Player player) {
        new MatchSpectateMenu().openMenu(player);
	}

}
