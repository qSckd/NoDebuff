package club.nodebuff.moon.commands.event.admin;

import club.nodebuff.moon.event.Event;
import club.nodebuff.moon.event.game.map.EventGameMap;
import club.nodebuff.moon.util.command.command.CPL;
import club.nodebuff.moon.util.command.command.CommandMeta;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "event add map", permission = "shadow.admin.event")
public class EventAddMapCommand {

	public void execute(Player player, @CPL("event") Event event, @CPL("map") EventGameMap gameMap) {
		if (event == null) {
			player.sendMessage(CC.RED + "An event type by that name does not exist.");
			player.sendMessage(CC.RED + "Types: sumo, spleef, brackets");
			return;
		}

		if (gameMap == null) {
			player.sendMessage(CC.RED + "A map with that name does not exist.");
			return;
		}

		if (!event.getAllowedMaps().contains(gameMap.getMapName())) {
			event.getAllowedMaps().add(gameMap.getMapName());
			event.save();

			player.sendMessage(CC.GOLD + "You successfully added the \"" + CC.GREEN + gameMap.getMapName() +
			                   CC.GOLD + "\" map from the \"" + CC.GREEN + gameMap.getMapName() + CC.GOLD +
			                   "\" event.");
		}
	}

}
