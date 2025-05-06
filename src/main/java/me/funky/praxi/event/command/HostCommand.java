package me.funky.praxi.event.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.event.game.menu.EventHostMenu;
import org.bukkit.entity.Player;

@CommandAlias("host|eventhost|hostevent|openevent|startevent|newevent|eventshost|hostevents")
@CommandPermission("shadow.event.host")
public class HostCommand extends BaseCommand {

    @Default
    public void host(Player player) {
		new EventHostMenu().openMenu(player);
    }
}