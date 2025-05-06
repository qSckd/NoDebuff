package club.nodebuff.moon.commands.user.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.profile.settings.menu.CosmeticsMenu;
import org.bukkit.entity.Player;

@CommandAlias("cosmetics")
@Description("Command to open your cosmetics menu.")
public class CosmeticsCommand extends BaseCommand {

    @Default
	public void open(Player player) {

        new CosmeticsMenu().openMenu(player);
    }

}
