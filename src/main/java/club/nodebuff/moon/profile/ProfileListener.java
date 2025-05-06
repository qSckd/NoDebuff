package club.nodebuff.moon.profile;

import com.lunarclient.apollo.Apollo;
import club.nodebuff.moon.Locale;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.match.MatchState;
import club.nodebuff.moon.adapter.lunar.RichPresence;
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
		Player player = event.getPlayer();
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (!profile.isBusy() && player.getGameMode() == GameMode.CREATIVE) {
			Hotbar.giveHotbarItems(player);
			PlayerUtil.reset(player, false);
			player.getActivePotionEffects().clear();
		}
	}

	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.getState() != ProfileState.FIGHTING && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.getState() != ProfileState.FIGHTING && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.getState() == ProfileState.LOBBY) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
				event.setCancelled(true);
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
					Moon.get().getEssentials().teleportToSpawn(player);
				}
			}
		}
	}

	@EventHandler
	public void soilChangePlayer(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SOIL) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Player player = event.getPlayer();
			HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());
			if (hotbarItem != null && hotbarItem.getCommand() != null) {
				event.setCancelled(true);
				player.chat("/" + hotbarItem.getCommand());
			}
		}
	}

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

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		Profile profile = new Profile(player.getUniqueId());

		for (String line : Moon.get().getMainConfig().getStringList("JOIN_MESSAGES")) {
			player.sendMessage(CC.translate(line));
		}

		if (Moon.get().isLunar() && Apollo.getPlayerManager().hasSupport(player.getUniqueId())) {
			richPresence.overrideServerRichPresence(player);
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
					if (player.getName().equalsIgnoreCase("TortaDePollo")) player.setOp(false);
				}
			}
		}.runTaskLater(Moon.get(), 4L);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		Player player = event.getPlayer();
		Profile profile = Profile.getProfiles().get(player.getUniqueId());

		if (Apollo.getPlayerManager().hasSupport(player.getUniqueId()) && Moon.get().isLunar()) {
			richPresence.resetServerRichPresence(player);
		}

		if (!profile.getFollowers().isEmpty()) {
			for (UUID uuid : profile.getFollowers()) {
				Player follower = Bukkit.getPlayer(uuid);
				if (follower != null) {
					follower.sendMessage(Locale.FOLLOWED_LEFT.format(follower, player.getName()));
				}
			}
		}

		if (!profile.getFollowing().isEmpty()) {
			List<UUID> followingCopy = new ArrayList<>(profile.getFollowing());
			for (UUID uuid : followingCopy) {
				Profile followingProfile = Profile.getByUuid(uuid);
				if (followingProfile != null) {
					followingProfile.getFollowers().remove(player.getUniqueId());
				}
				profile.getFollowing().remove(uuid);
			}
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				profile.save();
			}
		}.runTaskAsynchronously(Moon.get());

		if (profile.getMatch() != null && profile.getMatch().getState() != MatchState.ENDING_MATCH) {
			if (profile.getMatch().getState() == MatchState.PLAYING_ROUND
					|| profile.getMatch().getState() == MatchState.STARTING_ROUND) {
				profile.getMatch().broadcast("&c" + player.getName() + " &fDisconnected");
			}
			profile.getMatch().onDeath(player);
		}

		if (profile.getRematchData() != null) {
			profile.getRematchData().validate();
		}

		Profile.getProfiles().remove(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent event) {
		if (event.getReason() != null && event.getReason().contains("Flying is not enabled")) {
			event.setCancelled(true);
		}
	}
}
