package me.funky.praxi.commands.user.party;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.party.menu.PartyManageMenu;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.util.CC;
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

