package club.nodebuff.moon.profile.option.killeffect.menu;

import com.google.common.base.Preconditions;

import java.util.List;

import com.google.common.collect.Lists;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.option.killeffect.SpecialEffects;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KillEffectButton extends Button {

  private final SpecialEffects effect;

  public KillEffectButton(SpecialEffects effect) {
    this.effect = Preconditions.checkNotNull(effect, "effect");
  }

  public boolean shouldUpdate(Player player, ClickType clickType) {
    return true;
  }

  @Override
  public void clicked(Player player, ClickType clickType) {
    Profile profile = Profile.getByUuid(player.getUniqueId());
    if (!this.effect.hasPermission(player)) {
      player.sendMessage(CC.translate("&cNo permission."));
      return;
    }
    if (profile.getOptions().killEffect().getName().equals(this.effect.getName())) {
      player.sendMessage(CC.translate("&cThis kill effect is already in use."));
    } else {
      profile.getOptions().killEffect(this.effect);
      player.sendMessage(CC.translate("&fYou have selected &" + profile.getOptions().theme().getColor().getChar() + this.effect.getName() + " &fkill effect!"));
    }
  }

  @Override
  public ItemStack getButtonItem(Player player) {
    Profile profile = Profile.getByUuid(player.getUniqueId());
    List<String> lore = Lists.newArrayList();
    if (profile.getOptions().killEffect().getName().equals(this.effect.getName())) {
      lore.add(ChatColor.WHITE + "You have this equipped.");
      lore.add(ChatColor.WHITE + " ");
      lore.add(ChatColor.RED + "[Already equipped]");
    } else {
      lore.add(ChatColor.WHITE + "You don't have this equipped.");
      lore.add(ChatColor.WHITE + " ");
      lore.add(ChatColor.GREEN + "[Click to equip]");
    }
    return new ItemBuilder(effect.getIcon()).name(profile.getOptions().killEffect().getName().equals(this.effect.getName()) ? "&" + profile.getOptions().theme().getColor().getChar() + this.effect.getName() + ChatColor.GREEN + " (Equipped)" : "&" + profile.getOptions().theme().getColor().getChar()  + this.effect.getName()).lore(lore).build();
  }
}
