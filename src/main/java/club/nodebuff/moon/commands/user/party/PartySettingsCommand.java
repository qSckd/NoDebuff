package club.nodebuff.moon.commands.user.party;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.party.menu.PartyManageMenu;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("partymanage")
@Description("Command to open party manage menu.")
public class PartySettingsCommand extends BaseCommand {

   @Default
   public void manage(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getParty() == null) {
			player.sendMessage(CC.RED + "You do not have a party.");
			return;
		}

		if (!profile.getParty().getLeader().equals(player)) {
			player.sendMessage(CC.RED + "You are not the leader of your party.");
			return;
		}
		//new PartyManageMenu().openMenu(player);
	   new PartyManageMenu().openMenu(player);
    }
}

