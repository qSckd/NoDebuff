package club.nodebuff.moon.commands.event.admin;

import club.nodebuff.moon.util.command.command.CPL;
import club.nodebuff.moon.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "events", permission = "shadow.event.host")
public class EventsCommand {

	public void execute(Player player) {
		player.sendMessage("WIP");
	}

}
