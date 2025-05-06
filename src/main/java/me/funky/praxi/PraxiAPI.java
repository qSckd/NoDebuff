package me.funky.praxi;

import me.funky.praxi.adapter.spigot.SpigotManager;
import me.funky.praxi.arena.Arena;
import me.funky.praxi.kit.Kit;
import org.bukkit.entity.Player;

public class PraxiAPI {

    public static PraxiAPI INSTANCE;

    public static void setKnockbackProfile(Player player, String profile) {
        SpigotManager.getSpigot().setKnockback(player, profile);
    }

    public static Kit getKit(String name) {
        return Kit.getByName(name);
    }
    public static Arena getArena(String name) {
        return Arena.getByName(name);
    }

    public static Arena getRandomArena(Kit kit) {
        return Arena.getRandomArena(kit);
    }
}
