package me.funky.praxi.commands.user.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.Locale;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.ProfileState;
import me.funky.praxi.profile.settings.menu.SettingsMenu;
import me.funky.praxi.util.CC;
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