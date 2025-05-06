package me.funky.praxi.profile.coinshop.submenu.trails;

import me.funky.praxi.Praxi;
import me.funky.praxi.profile.option.trail.Trail;
import me.funky.praxi.util.Constants;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ProjectileTrailsPurchaseMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&8Purchase Projectile Trails";
    }

    public int size(Map<Integer, Button> buttons) {
        return 27;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        for (int k = 0; k < 27; k++) {
            buttons.put(k, Constants.BLACK_PANE);
        }
        int y = 1;
        int x = 1;
        for (Trail trail : Trail.values()) {
            if (trail.equals(Trail.NONE)) continue;
            buttons.put(this.getSlot(x++, y), new ProjectileTrailsPurchaseButton(trail));
            if (x != 8) continue;
            ++y;
            x = 1;
        }
        return buttons;
    }
}
