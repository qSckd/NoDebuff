package me.funky.praxi.queue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.funky.praxi.Praxi;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.queue.Queue;
import me.funky.praxi.queue.menu.QueueSelectKitMenu;
import me.funky.praxi.util.CC;
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