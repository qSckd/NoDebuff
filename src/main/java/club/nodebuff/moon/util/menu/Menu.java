// Decompiled with: CFR 0.152
// Class Version: 8
package club.nodebuff.moon.util.menu;

import java.util.HashMap;
import java.util.Map;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Menu {
	public static Map<String, Menu> currentlyOpenedMenus = new HashMap<String, Menu>();
	protected Moon moon = Moon.get();
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();
	private boolean autoUpdate = false;
	private boolean updateAfterClick = true;
	private boolean closedByMenu = false;
	private boolean placeholder = false;
	private Button placeholderButton = Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, " ");

	private ItemStack createItemStack(Player player, Button button) {
		ItemStack item = button.getButtonItem(player);
		if (item.getType() != Material.SKULL_ITEM) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null && meta.hasDisplayName()) {
				meta.setDisplayName(meta.getDisplayName() + "§b§c§d§e");
			}
			item.setItemMeta(meta);
		}
		return item;
	}

	public void openMenu(Player player) {
		this.buttons = this.getButtons(player);
		Menu previousMenu = currentlyOpenedMenus.get(player.getName());
		Inventory inventory = null;
		int size = this.getSize() == -1 ? Math.min(this.size(this.buttons), 54) : this.getSize();
		boolean update = false;
		String title = CC.translate(this.getTitle(player));
		if (title.length() > 32) {
			title = title.substring(0, 32);
		}
		if (player.getOpenInventory() != null) {
			if (previousMenu == null) {
				player.closeInventory();
			} else {
				int previousSize = player.getOpenInventory().getTopInventory().getSize();
				if (previousSize == size && player.getOpenInventory().getTopInventory().getTitle().equals(title)) {
					inventory = player.getOpenInventory().getTopInventory();
					update = true;
				} else {
					previousMenu.setClosedByMenu(true);
					player.closeInventory();
				}
			}
		}
		if (inventory == null) {
			inventory = Bukkit.createInventory((InventoryHolder)player, (int)size, (String)title);
		}
		inventory.setContents(new ItemStack[inventory.getSize()]);
		currentlyOpenedMenus.put(player.getName(), this);
		for (Map.Entry<Integer, Button> buttonEntry : this.buttons.entrySet()) {
			inventory.setItem(buttonEntry.getKey().intValue(), this.createItemStack(player, buttonEntry.getValue()));
		}
		if (this.isPlaceholder()) {
			for (int index = 0; index < size; ++index) {
				if (this.buttons.get(index) != null) continue;
				this.buttons.put(index, this.placeholderButton);
				inventory.setItem(index, this.placeholderButton.getButtonItem(player));
			}
		}
		if (update) {
			player.updateInventory();
		} else {
			player.openInventory(inventory);
		}
		this.onOpen(player);
		this.setClosedByMenu(false);
	}

	public int size(Map<Integer, Button> buttons) {
		int highest = 0;
		for (int buttonValue : buttons.keySet()) {
			if (buttonValue <= highest) continue;
			highest = buttonValue;
		}
		return (int)(Math.ceil((double)(highest + 1) / 9.0) * 9.0);
	}

	public int getSize() {
		return -1;
	}

	public int getSlot(int x, int y) {
		return 9 * y + x;
	}

	public abstract String getTitle(Player var1);

	public abstract Map<Integer, Button> getButtons(Player var1);

	public void onOpen(Player player) {
	}

	public void onClose(Player player) {
	}

	public Map<Integer, Button> getButtons() {
		return this.buttons;
	}

	public boolean isAutoUpdate() {
		return this.autoUpdate;
	}

	public boolean isUpdateAfterClick() {
		return this.updateAfterClick;
	}

	public boolean isClosedByMenu() {
		return this.closedByMenu;
	}

	public boolean isPlaceholder() {
		return this.placeholder;
	}

	public Button getPlaceholderButton() {
		return this.placeholderButton;
	}

	public void setmoon(Moon moon) {
		this.moon = moon;
	}

	public void setButtons(Map<Integer, Button> buttons) {
		this.buttons = buttons;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public void setUpdateAfterClick(boolean updateAfterClick) {
		this.updateAfterClick = updateAfterClick;
	}

	public void setClosedByMenu(boolean closedByMenu) {
		this.closedByMenu = closedByMenu;
	}

	public void setPlaceholder(boolean placeholder) {
		this.placeholder = placeholder;
	}

	public void setPlaceholderButton(Button placeholderButton) {
		this.placeholderButton = placeholderButton;
	}

	public Moon getmoon() {
		return this.moon;
	}
}
