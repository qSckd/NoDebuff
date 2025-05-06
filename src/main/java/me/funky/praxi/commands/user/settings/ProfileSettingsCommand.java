package me.funky.praxi.commands.user.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.Locale;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.settings.menu.ProfileSettingsMenu;
import org.bukkit.entity.Player;

@CommandAlias("profilesettings")
@Description("Open profile settings menu.")
public class ProfileSettingsCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
        new ProfileSettingsMenu().openMenu(player);
    }

}
