package club.nodebuff.moon.queue;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.Locale;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.profile.hotbar.Hotbar;
import club.nodebuff.moon.profile.hotbar.HotbarItem;
import club.nodebuff.moon.queue.menu.QueueSelectKitMenu;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.ReplaceUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR ||
				event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());

			if (hotbarItem != null) {
				Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
				boolean cancelled = true;

				if (hotbarItem == HotbarItem.QUEUE_JOIN_RANKED) {
                    if (profile.getWins() < Moon.get().getSettingsConfig().getInteger("RANKED.REQUIRED-WINS") && Moon.get().getSettingsConfig().getInteger("RANKED.REQUIRED-WINS") != 0) {
                        event.getPlayer().sendMessage(CC.translate(Moon.get().getMainConfig().getString("RANKED.ERROR-MESSAGE"))
                            .replace("{wins}", String.valueOf(Moon.get().getMainConfig().getInteger("RANKED.REQUIRED-WINS"))));
                        return;
                    }
					if (!profile.isBusy()) {
					  if (!profile.isRankedBan()) {
						new QueueSelectKitMenu(true, true).openMenu(event.getPlayer());
					} else {
							event.getPlayer().sendMessage(CC.translate("&cYou are permanently banned from ranked."));
							event.getPlayer().sendMessage(CC.translate("&cReason: &f" + profile.getRankedBanReason()));
							event.getPlayer().sendMessage(CC.translate("&cBan ID: &f" + profile.getRankedBanID()));
						}
					}
				} else if (hotbarItem == HotbarItem.QUEUE_JOIN_UNRANKED) {
					if (!profile.isBusy()) {
						new QueueSelectKitMenu(false, true).openMenu(event.getPlayer());
					}
				} else if (hotbarItem == HotbarItem.QUEUE_LEAVE) {
					if (profile.getState() == ProfileState.QUEUEING) {
						profile.getQueueProfile().getQueue().removePlayer(profile.getQueueProfile());
					}
				} else {
					cancelled = false;
				}

				event.setCancelled(cancelled);
			}
		}
	}

}
