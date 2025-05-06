package club.nodebuff.moon.commands.user.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Locale;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.profile.settings.menu.SettingsMenu;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("settings")
@Description("Command to open settings menu.")
public class SettingsCommand extends BaseCommand {

    @Default
    public void settings(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
		if (profile.getState() != ProfileState.LOBBY && !player.isOp()) {
			player.sendMessage(CC.translate("&cYou can use this comand in lobby only."));
			return;
		}
		new SettingsMenu().openMenu(player);
    }
}