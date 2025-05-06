package me.funky.praxi.commands.admin.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.Praxi;
import me.funky.praxi.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("setspawn")
@CommandPermission("shadow.admin.setspawn")
@Description("Set server spawn location.")
public class SetSpawnCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
        Praxi.get().getEssentials().setSpawn(player.getLocation());
        player.sendMessage(CC.translate("&bSpawn set successfully!"));
    }
}
