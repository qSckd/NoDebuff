package me.funky.praxi.profile.settings;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum Settings {
    SHOW_SCOREBOARD("Show Scoreboard", Material.ITEM_FRAME, "Enable or Disable Scoreboard."),
    ALLOW_SPECTATORS("Allow Spectators", Material.REDSTONE_TORCH_ON, "Allow players to spectate you."),
    SPECTATOR_MESSAGES("Toggle Spectators messages", Material.BOOK, "Show or hide spectators messages."),
	SHOW_PLAYERS("Toggle Players Visibility", Material.EYE_OF_ENDER, "Show or Hide players."),
    ALLOW_DUELS("Allow Duels", Material.DIAMOND_SWORD, "Allow Duel Requests."),
	TIME_CHANGE("Change Time", Material.WATCH, "Change Ping Range."),
	THEME("Select Theme", Material.BOOK, "Select a color to see it in messages.");

    private final String name;
    private final Material material;
    private final String description;

    private Settings(String name, Material material, String description) {
        this.name = name;
        this.material = material;
        this.description = description;
    }
}