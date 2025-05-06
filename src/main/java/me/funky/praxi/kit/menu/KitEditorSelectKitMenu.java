package me.funky.praxi.kit.menu;

import lombok.AllArgsConstructor;
import me.funky.praxi.Praxi;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.Constants;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class KitEditorSelectKitMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return Praxi.get().getMenusConfig().getString("KIT-EDITOR.TITLE");
	}

	@Override
    public int getSize() {
		return Praxi.get().getMenusConfig().getInteger("KIT-EDITOR.SIZE");
    }

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		for (int k = 0; k < getSize(); k++) {
			buttons.put(k, Constants.BLACK_PANE);
		}
		final int[] i = {10};
		Kit.getKits().forEach(kit -> {
			if (!kit.isEnabled() || !kit.getGameRules().isEditable()) return;
			while (i[0] == 17 || i[0] == 18 || i[0] == 26 || i[0] == 27 || i[0] == 36 || i[0] == 37) i[0]++;
			buttons.put(i[0]++, new KitDisplayButton(kit));
		});
		return buttons;
	}

	@AllArgsConstructor
	private class KitDisplayButton extends Button {

		private Kit kit;

		@Override
		public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lore = new ArrayList<>(Praxi.get().getMenusConfig().getStringList("KIT-EDITOR.LORE"));
	
			return new ItemBuilder(kit.getDisplayIcon())
                    .name(Praxi.get().getMenusConfig().getString("KIT-EDITOR.KIT-NAME").replace("{kit}", kit.getName()).replace("{theme}", CC.translate("&" + profile.getOptions().theme().getColor().getChar())))
					.lore(lore)
					.clearFlags()
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			player.closeInventory();

			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.getKitEditorData().setSelectedKit(kit);

			new KitManagementMenu(kit).openMenu(player);
		}

	}
}
