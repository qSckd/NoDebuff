package club.nodebuff.moon.adapter.spigot.impl;

import club.nodebuff.moon.adapter.spigot.Spigot;
import org.bukkit.entity.Player;
import xyz.refinedev.spigot.api.combat.ICombatProfile;
import xyz.refinedev.spigot.features.combat.CombatAPI;

public class CarbonSpigot implements Spigot {

    @Override
    public void setKnockback(Player player, String kb) {
        CombatAPI api = CombatAPI.getInstance();
        ICombatProfile<?, ?, ?> profile = api.getProfile(kb);
        if (profile == null) {
            profile = api.getDefaultProfile(player.getWorld());
        }
        api.setPlayerProfile(player, profile);
    }
}
