package me.funky.praxi.commands.user.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.Locale;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("togglescoreboard|tsb")
@Description("Toggle scoreboard.")
public class ToggleScoreboardCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getOptions().showScoreboard(!profile.getOptions().showScoreboard());

        if (profile.getOptions().showScoreboard()) {
            player.sendMessage(Locale.OPTIONS_SCOREBOARD_ENABLED.format());
        } else {
            player.sendMessage(Locale.OPTIONS_SCOREBOARD_DISABLED.format());
        }
    }

}
