package club.nodebuff.moon.commands.user.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.util.CC;
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
      player.sendMessage(CC.translate("&bv" + Moon.get().getDescription().getVersion() + " &r&fMade by&b " +Moon.get().getDescription().getAuthors().toString().replace("[", "").replace("]", "")));
      player.sendMessage(CC.translate("&7&m---------------------------------"));
    }

    @Subcommand("reload")
    @CommandPermission("moon.admin.reload")
	public void reload(Player player) {

		Moon.get().configsLoad();
        player.sendMessage(CC.translate("&aSuccessfully reloaded configs!"));
    }

}
