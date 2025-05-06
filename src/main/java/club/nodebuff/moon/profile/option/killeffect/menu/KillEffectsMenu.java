package club.nodebuff.moon.profile.option.killeffect.menu;

import java.util.HashMap;
import java.util.Map;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.profile.option.killeffect.SpecialEffects;
import club.nodebuff.moon.util.Constants;
import club.nodebuff.moon.util.menu.Button;
import club.nodebuff.moon.util.menu.Menu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KillEffectsMenu extends Menu {

  public KillEffectsMenu() {

    this.setAutoUpdate(true);
  }

  @Override
  public boolean isAutoUpdate() {
    return true;
  }

  @NotNull
  public String getTitle(@NotNull Player player) {
    return "&8Kill Effects";
  }

  @NotNull
  public Map<Integer, Button> getButtons(@NotNull Player player) {
    HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
    int y = 1;
    int x = 1;
    for (SpecialEffects effects : SpecialEffects.values()) {
      if (effects.hasPermission(player)) {
        buttons.put(this.getSlot(x++, y), new KillEffectButton(effects));
      }
      if (x != 8) continue;
      ++y;
      x = 1;
    }
    for (int i = 0; i < 36; ++i) {
      buttons.putIfAbsent(i, Constants.BLACK_PANE);
    }
    return buttons;
  }

  public int size(Map<Integer, Button> buttons) {
    return 36;
  }
}
