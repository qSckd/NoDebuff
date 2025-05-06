package club.nodebuff.moon.commands.event.map;

import club.nodebuff.moon.event.game.map.EventGameMap;
import club.nodebuff.moon.util.command.command.CPL;
import club.nodebuff.moon.util.command.command.CommandMeta;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "event map delete", permission = "practice.admin.event")
public class EventMapDeleteCommand {

	public void execute(Player player, @CPL("map") EventGameMap gameMap) {
		if (gameMap == null) {
			player.sendMessage(CC.RED + "An event map with that name already exists.");
			return;
		}

		gameMap.delete();

		EventGameMap.getMaps().remove(gameMap);

		player.sendMessage(CC.GREEN + "You successfully deleted the event map \"" + gameMap.getMapName() + "\".");
	}

}
