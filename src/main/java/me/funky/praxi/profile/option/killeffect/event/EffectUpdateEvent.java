package club.nodebuff.moon.profile.option.killeffect.event;

import com.google.common.base.Preconditions;
import club.nodebuff.moon.profile.option.killeffect.SpecialEffects;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class EffectUpdateEvent extends PlayerEvent {
  public static HandlerList getHandlerList() {
    return handlerList;
  }
  
  private static HandlerList handlerList = new HandlerList();
  
  private final SpecialEffects setting;
  
  public SpecialEffects getSetting() {
    return this.setting;
  }
  
  public EffectUpdateEvent(Player player, SpecialEffects setting) {
    super(player);
    this.setting = (SpecialEffects)Preconditions.checkNotNull(setting, "effect");
  }
  
  public HandlerList getHandlers() {
    return handlerList;
  }
}
