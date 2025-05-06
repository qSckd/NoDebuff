package club.nodebuff.moon;

import club.nodebuff.moon.adapter.spigot.SpigotManager;
import club.nodebuff.moon.arena.Arena;
import club.nodebuff.moon.kit.Kit;
import org.bukkit.entity.Player;

public class MoonAPI {

    public static MoonAPI INSTANCE;

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
