package club.nodebuff.moon.commands.admin.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandAlias("rename")
@CommandPermission("shadow.admin.rename")
@Description("Rename the item in hand.")
public class RenameCommand extends BaseCommand {

	@Default
    public void execute(Player player, String name) {

		if (name == null) {
            player.sendMessage(CC.translate(Moon.get().getMainConfig().getString("COMMANDS.RENAME.NOT-VALID")));
			return;
		}

		if (player.getItemInHand() != null) {
			ItemStack itemStack = player.getItemInHand();
			ItemMeta itemMeta = itemStack.getItemMeta();
			StringBuilder string = new StringBuilder();
			string.append(name).append(" ");
			itemMeta.setDisplayName(CC.translate(string.toString()));
			itemStack.setItemMeta(itemMeta);

			player.updateInventory();
            player.sendMessage(CC.translate(Moon.get().getMainConfig().getString("COMMANDS.RENAME.RENAMED")));
		} else {
            player.sendMessage(CC.translate(Moon.get().getMainConfig().getString("COMMANDS.RENAME.ERROR")));
		}
	}
}
