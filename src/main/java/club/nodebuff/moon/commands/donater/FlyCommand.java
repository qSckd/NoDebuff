package club.nodebuff.moon.commands.donater;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("fly|togglefly|flytoggle")
@CommandPermission("shadow.donor.fly")
@Description("Toggle fly.")
public class FlyCommand extends BaseCommand {

    @Default
	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
			if (player.getAllowFlight()) {
				player.setAllowFlight(false);
				player.setFlying(false);
				player.updateInventory();
				player.sendMessage(CC.RED + "You are no longer flying.");
			} else {
				player.setAllowFlight(true);
				player.setFlying(true);
				player.updateInventory();
				player.sendMessage(CC.GREEN + "You are now flying.");
			}
		} else {
			player.sendMessage(CC.RED + "You can't fly in your current state.");
		}
	}

}
