package club.nodebuff.moon.divisions.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.divisions.menu.ProfileDivisionsMenu;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/* Author: HmodyXD
   Project: Shadow
   Date: 2024/5/26
 */

@CommandAlias("division|divisions")
@Description("Command to open divisions menu.")
public class DivisionsCommand extends BaseCommand {

    public void divisions(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
		    new ProfileDivisionsMenu(profile).openMenu(player);
       
    }
}
