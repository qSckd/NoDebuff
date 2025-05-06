package club.nodebuff.moon.commands.admin.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("setholo|sethologram|hologram|holo|holograms")
@CommandPermission("shadow.admin.holograms")
@Description("Set hologram location.")
public class SetHoloCommand extends BaseCommand {

    @HelpCommand
    @Syntax("[page]")
    public void help(Player player, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("welcome")
    public void welcome(Player player) {
        Moon.get().getEssentials().setMainHolo(player.getLocation());
        player.sendMessage(CC.translate("&bWelcome Holo location set successfully!"));
    }

    @Subcommand("leaderboard")
    public void leaderboard(Player player) {
        Moon.get().getEssentials().setHolo(player.getLocation());
        player.sendMessage(CC.translate("&bLeaderboards Holo location set successfully!"));
    }
}
