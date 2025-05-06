package me.funky.praxi.util.hologram;

import java.beans.ConstructorProperties;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class HologramTask implements Runnable {
    private final HologramHandler hologramHandler;

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = player.getWorld();
            for (Hologram hologram : this.hologramHandler.getHolograms()) {
                Location location = hologram.getLocation();
                if (world.getUID() != location.getWorld().getUID()) {
                    hologram.hide(player);
                    continue;
                }
                Location playerLocation = player.getLocation();
                if (playerLocation.distanceSquared(location) <= 1600.0) {
                    if (hologram.isSetup(player.getUniqueId())) continue;
                    hologram.setup(player);
                    continue;
                }
                hologram.hide(player);
            }
        }
    }

    public HologramHandler getHologramHandler() {
        return this.hologramHandler;
    }

    @ConstructorProperties(value={"hologramHandler"})
    public HologramTask(HologramHandler hologramHandler) {
        this.hologramHandler = hologramHandler;
    }
}