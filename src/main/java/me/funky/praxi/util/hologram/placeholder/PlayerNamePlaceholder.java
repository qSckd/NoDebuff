package me.funky.praxi.util.hologram.placeholder;

import org.bukkit.entity.Player;

public class PlayerNamePlaceholder implements Placeholder {

    @Override
    public String formatLine(Player player, String line) {
        return line.replace("%DISPLAY_NAME%", player.getName());
    }
}
