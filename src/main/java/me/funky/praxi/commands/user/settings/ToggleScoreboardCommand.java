package club.nodebuff.moon.commands.user.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Locale;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.CC;
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
