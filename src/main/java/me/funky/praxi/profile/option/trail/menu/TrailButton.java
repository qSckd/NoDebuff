package me.funky.praxi.profile.option.trail.menu;

import lombok.AllArgsConstructor;
import com.google.common.collect.Lists;
import me.funky.praxi.Praxi;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.option.trail.Trail;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class TrailButton extends Button {

    private final Trail trail;

    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!this.trail.hasPermission(player)) {
            player.sendMessage(ChatColor.RED + "No permission.");
            return;
        }
        if (profile.getOptions().trail().getName().equals(this.trail.getName())) {
            player.sendMessage(ChatColor.RED + "This trail is already in use.");
        } else {
            profile.getOptions().trail(this.trail);
            player.sendMessage(ChatColor.GREEN + "You have selected the " + ChatColor.YELLOW + this.trail.getName() + ChatColor.GREEN + " trail!");
        }
    }


    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        List<String> lore = Lists.newArrayList();
        if (profile.getOptions().trail().getName().equals(this.trail.getName())) {
            lore.add(ChatColor.GRAY + "You have this equipped.");
            lore.add("");
            lore.add(ChatColor.GREEN + "[Already equipped]");
        } else {
            lore.add(ChatColor.GRAY + "You don't have this equipped.");
            lore.add("");
            lore.add(ChatColor.GREEN + "[Click to equip]");
        }
        return new ItemBuilder(trail.getIcon()).name(profile.getOptions().trail().getName().equals(this.trail.getName()) ? "&b" +
                this.trail.getName() + ChatColor.GREEN + " (Equipped)" : "TODO: Add theme to this class" + this.trail.getName()).lore(lore).build();
    }
}
