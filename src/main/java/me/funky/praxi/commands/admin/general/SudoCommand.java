package me.funky.praxi.commands.admin.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.Praxi;
import me.funky.praxi.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("sudo")
@CommandPermission("shadow.admin.sudo")
@Description("Force player to say custom message or use custom command")
public class SudoCommand extends BaseCommand {

   @Default
   @Syntax("<target> <message/command>")
   public void command(Player player, String target, String messagecmd) {
       Player user = Bukkit.getPlayer(target);
	   user.chat(messagecmd);
       player.sendMessage(CC.translate(Praxi.get().getMainConfig().getString("COMMANDS.SUDO.MESSAGE")
	   .replace("{target}", user.getName())
	   .replace("{message_cmd}", messagecmd)
	   .replace("{meteor}", "meteor is the best developer!!")));
   }
}