package me.funky.praxi.commands.user.match;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.match.MatchSnapshot;
import me.funky.praxi.match.menu.MatchDetailsMenu;
import me.funky.praxi.util.CC;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("viewinv")
@Description("View post match inventories.")
public class ViewInventoryCommand extends BaseCommand {

    @Default
    @Syntax("<uuid>")
    public void execute(Player player, String id) {
        MatchSnapshot cachedInventory;

        try {
            cachedInventory = MatchSnapshot.getByUuid(UUID.fromString(id));
        } catch (Exception e) {
            cachedInventory = MatchSnapshot.getByName(id);
        }

        if (cachedInventory == null) {
            player.sendMessage(CC.RED + "Couldn't find an inventory for that ID.");
            return;
        }

        new MatchDetailsMenu(cachedInventory, null).openMenu(player);
    }
}
