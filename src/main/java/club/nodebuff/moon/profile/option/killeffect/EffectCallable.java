package club.nodebuff.moon.profile.option.killeffect;

import org.bukkit.entity.Player;

public interface EffectCallable {
  void call(Player paramPlayer, Player... paramVarArgs);
}
