package club.nodebuff.moon.util.hologram;

import java.beans.ConstructorProperties;

import club.nodebuff.moon.util.TaskUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class HologramListener implements Listener {
    private HologramHandler hologramHandler;

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Hologram hologram : this.hologramHandler.getHolograms()) {
            Location location = hologram.getLocation();
            if (!player.getWorld().getUID().equals(location.getWorld().getUID())) {
                hologram.hide(player);
                continue;
            }
            hologram.setup(event.getPlayer());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        this.updateWithDelay(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        this.updateWithDelay(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.hologramHandler.handleMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (Hologram hologram : this.hologramHandler.getHolograms()) {
            hologram.hide(event.getPlayer());
        }
    }

    private void updateWithDelay(Player player) {
        TaskUtil.runLaterAsync(() -> {
            for (Hologram hologram : this.hologramHandler.getHolograms()) {
                Location location = hologram.getLocation();
                if (!player.getWorld().getUID().equals(location.getWorld().getUID())) {
                    hologram.hide(player);
                    continue;
                }
                hologram.setup(player);
            }
        }, 5);
    }

    @ConstructorProperties(value={"hologramHandler"})
    public HologramListener(HologramHandler hologramHandler) {
        this.hologramHandler = hologramHandler;
    }
}