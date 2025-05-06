package club.nodebuff.moon.queue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.queue.Queue;
import club.nodebuff.moon.queue.menu.QueueSelectKitMenu;
import club.nodebuff.moon.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

@CommandAlias("queue")
@Description("command to open queue menus.")
public class QueueCommand extends BaseCommand {

    @HelpCommand
    @Syntax("[page]")
    public void help(Player player, CommandHelp help) {
        help.showHelp();
    }

	@Subcommand("unranked")
    public void unranked(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isBusy()) {
            player.sendMessage(CC.translate("You cannot queue right now."));
        } else {
            new QueueSelectKitMenu(false, true).openMenu(player);
        }
    }

    @Subcommand("ranked")
    public void ranked(Player player) {
      Profile profile = Profile.getByUuid(player.getUniqueId());
      if (profile.isBusy()) {
          player.sendMessage(CC.translate("You cannot queue right now."));
      } else {
          new QueueSelectKitMenu(true, true).openMenu(player);
      }
   }
}