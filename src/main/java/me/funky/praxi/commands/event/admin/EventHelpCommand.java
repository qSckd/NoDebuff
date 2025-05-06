package club.nodebuff.moon.commands.event.admin;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.util.command.command.CPL;
import club.nodebuff.moon.util.command.command.CommandMeta;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "event", "event help" })
public class EventHelpCommand {

	public void execute(Player player) {
		for (String line : Moon.get().getMainConfig().getStringList("EVENT.HELP")) {
			player.sendMessage(CC.translate(line));
		}
	}

}
