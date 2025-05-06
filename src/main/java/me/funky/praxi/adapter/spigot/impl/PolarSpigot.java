/*package me.funky.praxi.adapter.spigot.impl;

import me.funky.praxi.adapter.spigot.Spigot;
import me.funky.praxi.util.CC;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import me.zatrex.spigot.PolarSpigot;
import me.zatrex.spigot.knockback.KnockbackProfile;

public class PolarSpigot implements Spigot {

    @Override
    public void setKnockback(Player player, String kb) {
        PolarSpigot spigot = PolarSpigot.INSTANCE;
        KnockbackProfile profile = spigot.getKbProfileByName(kb);
        
        if (profile == null) {
            player.sendMessage(CC.translate("Invalid kb profile! " + kb + "."));
            return;
        }
        
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityLiving living = craftPlayer.getHandle();

        if (player.isOnline()) {
            living.setKnockbackProfile(profile);
        }
    }
}
*/