package me.funky.praxi.commands.user.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.Praxi;
import me.funky.praxi.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@CommandAlias("practice|moon|prac|versedev|zatrex|123456789|12345678|ver|version")
@Description("Command to manage the plugin.")
public class PracticeCommand extends BaseCommand {

    @Default
	public void main(Player player) {

      player.sendMessage(CC.translate("&7&m---------------------------------"));
      player.sendMessage(CC.translate("&fThis server is running &r&b&lMoon"));
      player.sendMessage(CC.translate("&bv" + Praxi.get().getDescription().getVersion() + " &r&fMade by&b " +Praxi.get().getDescription().getAuthors().toString().replace("[", "").replace("]", "")));
      player.sendMessage(CC.translate("&7&m---------------------------------"));
    }

    @Subcommand("reload")
    @CommandPermission("moon.admin.reload")
	public void reload(Player player) {

		Praxi.get().configsLoad();
        player.sendMessage(CC.translate("&aSuccessfully reloaded configs!"));
    }

}
