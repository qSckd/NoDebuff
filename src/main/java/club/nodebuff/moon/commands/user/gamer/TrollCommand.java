package club.nodebuff.moon.commands.user.gamer;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/* Author: HmodyXD
   Project: Shadow
   Date: 2024/5/26
 */

@CommandAlias("troll|trollplayer|trolluser|t")
@CommandPermission("shadow.admin.troll")
@Description("Command to troll players.")
public class TrollCommand extends BaseCommand {

    @Default
    @Syntax("<target>")
    @CommandCompletion("@players")
    public void troll(Player player, String targetPlayer) {
        Player target = Bukkit.getPlayer(targetPlayer);
        if (target == null) {
            player.sendMessage(CC.translate("This player if currently offline!"));
            return;
        }

        try {
            String path = Bukkit.getServer().getClass().getPackage().getName();
            String version = path.substring(path.lastIndexOf(".") + 1);
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Class<?> PacketPlayOutGameStateChange = Class.forName("net.minecraft.server." + version + ".PacketPlayOutGameStateChange");
            Class<?> Packet = Class.forName("net.minecraft.server." + version + ".Packet");

            Constructor<?> playOutConstructor = PacketPlayOutGameStateChange.getConstructor(Integer.TYPE, Float.TYPE);
            Object packet = playOutConstructor.newInstance(5, 0);

            Object craftPlayerObject = craftPlayer.cast(target);
            Method getHandleMethod = craftPlayer.getMethod("getHandle");
            Object handle = getHandleMethod.invoke(craftPlayerObject);
            Object pc = handle.getClass().getField("playerConnection").get(handle);
            Method sendPacketMethod = pc.getClass().getMethod("sendPacket", Packet);
            sendPacketMethod.invoke(pc, packet);

            player.sendMessage(CC.translate("&fYou have trolled &b" + target.getName()));
			//target.sendMessage(CC.translate("&fYou got trolled by &b" + player.getName())); i wont add this now
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(CC.translate("An error occurred while trying to troll the player.")); // this will work only in 1.8.8 spigots you will get this error if you use another version
        }
    }
}
