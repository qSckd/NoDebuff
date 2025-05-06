package club.nodebuff.moon.commands.admin.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("setspawn")
@CommandPermission("shadow.admin.setspawn")
@Description("Set server spawn location.")
public class SetSpawnCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
        Moon.get().getEssentials().setSpawn(player.getLocation());
        player.sendMessage(CC.translate("&bSpawn set successfully!"));
    }
}
