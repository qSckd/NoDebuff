package me.funky.praxi.profile.coinshop.submenu.trails;

import com.google.common.collect.Lists;
import me.funky.praxi.Praxi;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.option.trail.Trail;
import me.funky.praxi.util.PurchaseUtil;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.PlayerUtil;
import me.funky.praxi.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;

@AllArgsConstructor
public class ProjectileTrailsPurchaseButton extends Button {

    private final Trail trail;

    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (this.trail.hasPermission(player)) {
          player.sendMessage(ChatColor.RED + "You already have this one.");
          return;
        }
        PurchaseUtil.purchaseItem(player, trail);
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        List<String> lore = Lists.newArrayList();
        final int previousPrice = trail.getPrice();
        for (Kit kit : Kit.getKits()) {
            if (profile.getKitData().get(kit).getWinstreak() >= 15 && trail.getPrice() > 300) {
                trail.setPrice(trail.getPrice() - 100);
                break;
            }
        }
        if (profile.hasPermission(trail.getPermission())) {
            lore.add(ChatColor.WHITE + " ");
            lore.add(ChatColor.AQUA + "Cost");
            lore.add(ChatColor.GOLD + " ┃ &fPrice: " + ChatColor.AQUA + trail.getPrice() + "$");
            lore.add(ChatColor.WHITE + " ");
            lore.add(ChatColor.RED + "Already purchased!");
        } else {
            lore.add(ChatColor.GRAY + " ");
            lore.add(ChatColor.AQUA + "Cost");
            lore.add(ChatColor.GOLD + " ┃ &fPrice: " + (previousPrice == trail.getPrice() ? "&b" + trail.getPrice() :  CC.GRAY + CC.STRIKE_THROUGH + previousPrice + CC.RESET + " " + "&b" + trail.getPrice()) + "$");
            lore.add(ChatColor.WHITE + " ");
            lore.add(profile.getCoins() >= trail.getPrice() ? ChatColor.GREEN + "Click to purchase!" : ChatColor.RED + "You don't have enough coins.");
        }
        return new ItemBuilder(trail.getIcon()).name("&b" + this.trail.getName()).lore(lore).build();
    }
}
