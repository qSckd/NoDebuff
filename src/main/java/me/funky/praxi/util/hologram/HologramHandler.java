package me.funky.praxi.util.hologram;

import me.funky.praxi.Praxi;
import me.funky.praxi.util.TaskUtil;
import me.funky.praxi.util.hologram.placeholder.Placeholder;
import me.funky.praxi.util.hologram.placeholder.PlayerNamePlaceholder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class HologramHandler {
    private final Int2ObjectMap<Hologram> hologramMap = new Int2ObjectOpenHashMap<>();
    private final Set<Placeholder> placeholders = new HashSet<Placeholder>();

    public HologramHandler(Praxi instance) {
        this.registerPlaceholder(new PlayerNamePlaceholder());
        instance.getServer().getPluginManager().registerEvents(new HologramListener(this), instance);
        TaskUtil.scheduleAtFixedRateOnPool(new HologramTask(this), 50L, 50L, TimeUnit.MILLISECONDS);
    }

    public void registerPlaceholder(Placeholder placeholder) {
        this.placeholders.add(placeholder);
    }

    public int registerHologram(Hologram hologram) {
        return this.registerHologram(this.hologramMap.size(), hologram);
    }

    public int registerHologram(int index, Hologram hologram) {
        this.hologramMap.put(index, hologram);
        for (Player player : Bukkit.getOnlinePlayers()) {
            hologram.setup(player);
        }
        return index;
    }

    public void removeHologram(int index) {
        Hologram hologram = this.getHologram(index);
        if (hologram != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                hologram.hide(player);
            }
        }
        this.hologramMap.remove(index);
    }

    public Hologram getHologram(int id) {
        return (Hologram)this.hologramMap.get(id);
    }

    public Set<Hologram> getHolograms() {
        return new HashSet<Hologram>(this.hologramMap.values());
    }

    public void handleMovement(Player player, Location from, Location to) {
        if (from.getBlockZ() == to.getBlockZ() && from.getBlockX() == to.getBlockX()) {
            return;
        }
        for (Hologram hologram : this.getHolograms()) {
            Location location = hologram.getLocation();
            if (to.getWorld().getUID() != location.getWorld().getUID()) {
                hologram.hide(player);
                continue;
            }
            if (to.distanceSquared(location) <= 1600.0) {
                if (hologram.isSetup(player.getUniqueId())) continue;
                hologram.setup(player);
                continue;
            }
            hologram.hide(player);
        }
    }

    public Set<Placeholder> getPlaceholders() {
        return this.placeholders;
    }
}