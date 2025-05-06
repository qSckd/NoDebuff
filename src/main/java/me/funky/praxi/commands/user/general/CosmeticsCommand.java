package me.funky.praxi.commands.user.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.profile.settings.menu.CosmeticsMenu;
import org.bukkit.entity.Player;

@CommandAlias("cosmetics")
@Description("Command to open your cosmetics menu.")
public class CosmeticsCommand extends BaseCommand {

    @Default
	public void open(Player player) {

        new CosmeticsMenu().openMenu(player);
    }

}
