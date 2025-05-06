package me.funky.praxi.commands.event.admin;

import me.funky.praxi.util.command.command.CPL;
import me.funky.praxi.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "events", permission = "shadow.event.host")
public class EventsCommand {

	public void execute(Player player) {
		player.sendMessage("WIP");
	}

}
