package me.funky.praxi.commands.user.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CommandAlias("msg|message")
@Description("Command to message players.")
public class MsgCommand extends BaseCommand {

    private final Map<Player, Player> playerReplyMap = new HashMap<>();

    @CommandAlias("msg|message")
	@CommandCompletion("@players")
    @Syntax("<target> <message>")
	public void msg(Player player, String target, String message) {
		Player targetPlayer = Bukkit.getPlayer(target);

		if (targetPlayer == null) {
           player.sendMessage(CC.translate("&cThis player is currently offline."));
           return;
    	} else if (targetPlayer == player) {
			player.sendMessage(CC.translate("&cYou can't message yourself."));
			return;
	    }

        player.sendMessage(CC.translate("&b(To) &f" + target + " &b" + message));
		targetPlayer.sendMessage(CC.translate("&b(From) &f" + player.getName() + " &b" + message));
    }

    @CommandAlias("r|reply")
    @Syntax("<message>")
	public void reply(Player player, String message) {
		Player targetPlayer = playerReplyMap.get(player);

        if (targetPlayer == null) {
            player.sendMessage(CC.translate("&cThis player is currently offline."));
            return;
        } else if (targetPlayer == player) {
            player.sendMessage(CC.translate("&cYou can't message yourself."));
            return;
        }

        player.sendMessage(CC.translate("&b(To) &f" + targetPlayer.getName() + " &b" + message));
        targetPlayer.sendMessage(CC.translate("&b(From) &f" + player.getName() + " &b" + message));
    }
}
