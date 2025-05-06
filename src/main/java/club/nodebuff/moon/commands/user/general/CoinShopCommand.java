package club.nodebuff.moon.commands.user.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.profile.coinshop.CoinShopMenu;
import org.bukkit.entity.Player;

@CommandAlias("coinshop|shop|buy|cosmeticsstore")
@Description("Command to open shop menu.")
public class CoinShopCommand extends BaseCommand {

    @Default
	public void open(Player player) {

		new CoinShopMenu().openMenu(player);
    }

}
