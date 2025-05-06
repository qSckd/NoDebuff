package me.funky.praxi.queue.menu;

import lombok.AllArgsConstructor;
import me.funky.praxi.Praxi;
import me.funky.praxi.match.Match;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.queue.Queue;
import me.funky.praxi.util.CC;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.Constants;
import me.funky.praxi.util.menu.Button;
import me.funky.praxi.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class QueueSelectKitMenu extends Menu {

	private boolean ranked;
    private boolean solo;

	{
		setAutoUpdate(true);
	}

	@Override
	public String getTitle(Player player) {
		return "&8Select a kit (" + (ranked ? "Ranked" : "Unranked") + ")";
	}

	@Override
    public int getSize() {
		return Praxi.get().getMenusConfig().getInteger("QUEUE.SIZE");
    }

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 10;

		for (int j = 0; j < getSize(); ++j) {
			buttons.put(j, Constants.BLACK_PANE);
		}
        //if (solo) {
		    for (Queue queue : Queue.getQueues()) {
			    if (queue.isRanked() == ranked) {
				    if (ranked && !queue.getKit().getGameRules().isRanked()) continue;
				    while (i == 17 || i == 18 || i == 27 || i == 36) {
					    i++;
				    }
                    buttons.put(queue.getKit().getSlot(), new SelectKitButton(queue, solo));
                    if (!ranked) {
                        buttons.put(4, new SelectQueueButton(solo));
                    }
			    }
		    }
        /*} else {
            for (Queue queue : Queue.getQueues()) {
                //if (queue.isDuos()) {
                    buttons.put(queue.getKit().getSlot(), new SelectKitButton(queue, solo));
                    if (!ranked) {
                    buttons.put(4, new SelectQueueButton(solo));
                }
            }*/
        //}
		return buttons;
	}

	@AllArgsConstructor
	private class SelectKitButton extends Button {

		private Queue queue;
        private boolean solo;

		@Override
		public ItemStack getButtonItem(Player player) {
			List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(player.getUniqueId());
			lore.addAll(queue.getKit().getDescription());
			lore.add("");
			lore.add(" &fIn Fights: &r" + CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + Match.getInFightsCount(queue));
			lore.add(" &fIn Queue: &r" + CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + queue.getPlayers().size());
			lore.add("");
			lore.add(" &aClick here to play.");

			return new ItemBuilder(queue.getKit().getDisplayIcon())
					.name(CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + queue.getKit().getName())
					.lore(lore)
					.clearFlags()
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile == null) {
				return;
			}

            if(!profile.getFollowing().isEmpty()){
                player.sendMessage(CC.translate("&cYou can't queue while following someone."));
                return;
            }

			if (player.hasMetadata("frozen")) {
				player.sendMessage(CC.RED + "You can't queue while frozen.");
				return;
			}

			if (profile.isBusy()) {
				player.sendMessage(CC.RED + "You can't queue right now.");
				return;
			}

			player.closeInventory();
            if (solo) {
			    queue.addPlayer(player, queue.isRanked() ? profile.getKitData().get(queue.getKit()).getElo() : 0, false);
                queue.addQueue();
            } else {
                //player.sendMessage(CC.translate("&c&lComing Soon."));
                queue.addPlayer(player, queue.isRanked() ? profile.getKitData().get(queue.getKit()).getElo() : 0, true);
                queue.addQueue();
            }
		}
	}

    @AllArgsConstructor
	private class SelectQueueButton extends Button {

        private boolean solo;

		@Override
		public ItemStack getButtonItem(Player player) {
			List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(player.getUniqueId());
			lore.add("");
            if (solo) {
			    lore.add("&a■ &fSolos &7(1v1)");
			    lore.add("&7■ &fDuos &7(2v2)");
            } else {
                lore.add("&7■ &fSolos &7(1v1)");
			    lore.add("&a■ &fDuos &7(2v2)");
            }
			lore.add("");
			lore.add("&aClick to change.");

			return new ItemBuilder(Material.COMPASS)
					.name(CC.translate("&" + profile.getOptions().theme().getColor().getChar()) + "Select Queue")
					.lore(lore)
					.clearFlags()
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile == null) {
				return;
			}
            if (solo) {
                new QueueSelectKitMenu(false, false).openMenu(player);
            } else {
                new QueueSelectKitMenu(false, true).openMenu(player);
            }
		}
	}
}