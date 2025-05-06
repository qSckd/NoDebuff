package me.funky.praxi.commands.user.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.funky.praxi.profile.coinshop.CoinShopMenu;
import org.bukkit.entity.Player;

@CommandAlias("coinshop|shop|buy|cosmeticsstore")
@Description("Command to open shop menu.")
public class CoinShopCommand extends BaseCommand {

    @Default
	public void open(Player player) {

		new CoinShopMenu().openMenu(player);
    }

}
