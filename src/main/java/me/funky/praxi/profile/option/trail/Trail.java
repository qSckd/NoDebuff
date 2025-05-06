package me.funky.praxi.profile.option.trail;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.funky.praxi.profile.Profile;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public enum Trail {
    NONE("None", Material.RECORD_11,"", null),
    HEART("Heart", Material.RED_ROSE,"heart", Effect.HEART, 500),
    NOTES("Notes", Material.NOTE_BLOCK,"notes", Effect.NOTE, 750),
    BLOOD("Blood", Material.REDSTONE,"blood", Effect.COLOURED_DUST, 750),
    WATER("Water", Material.WATER_BUCKET,"water", Effect.WATERDRIP, 900);

    private String name;
    private Material icon;
    private String permission;
    private Effect trail;
    private int price;

    Trail(String name, Material icon, String permission, Effect trail) {
        this.name = name;
        this.icon = icon;
        this.permission = permission;
        this.trail = trail;
    }

    public void setPrice(int p) {
        this.price = p;
    }

    public boolean hasPermission(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        return profile.hasPermission(this.permission) || this.permission.isEmpty();
    }

}
