package me.funky.praxi.profile.themes;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum Themes {
    GOLD("Gold", ChatColor.GOLD),
    GREEN("Green", ChatColor.GREEN),
    AQUA("Aqua", ChatColor.AQUA),
    RED("Red", ChatColor.RED),
    YELLOW("Yellow", ChatColor.YELLOW),
    PURPLE("Purple", ChatColor.DARK_PURPLE),
    PINK("Pink", ChatColor.LIGHT_PURPLE),
    BLACK("Black", ChatColor.BLACK);


    private final String name;
    private final ChatColor color;

    Themes(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }
}
