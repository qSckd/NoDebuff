package club.nodebuff.moon.commands.user.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Locale;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.settings.menu.ProfileSettingsMenu;
import org.bukkit.entity.Player;

@CommandAlias("profilesettings")
@Description("Open profile settings menu.")
public class ProfileSettingsCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
        new ProfileSettingsMenu().openMenu(player);
    }

}
