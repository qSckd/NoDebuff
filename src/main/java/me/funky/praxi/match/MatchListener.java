package club.nodebuff.moon.match;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.Locale;
import club.nodebuff.moon.arena.Arena;
import club.nodebuff.moon.kit.Kit;
import club.nodebuff.moon.kit.KitLoadout;
import club.nodebuff.moon.match.menu.ViewInventoryMenu;
import club.nodebuff.moon.match.participant.MatchGamePlayer;
import club.nodebuff.moon.participant.GameParticipant;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.profile.ProfileState;
import club.nodebuff.moon.profile.hotbar.Hotbar;
import club.nodebuff.moon.profile.hotbar.HotbarItem;
import club.nodebuff.moon.util.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;

public class MatchListener implements Listener {

	protected boolean end;

	public MatchListener() {
		this.end = false;
	}

	@EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Match match = profile.getMatch();

        if (profile.getMatch() != null) {
            Player rival = match.getOpponent(player);
            boolean aTeam = match.getParticipantA().containsPlayer(player.getUniqueId());
            if (profile.getMatch().getKit().getGameRules().isSumo() ||
                profile.getMatch().getKit().getGameRules().isSpleef()) {
                Location playerLocation = event.getPlayer().getLocation();
                Block block = playerLocation.getBlock();

                if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
                    profile.getMatch().onDeath(player);
                }
            }

            if (match.getKit().getGameRules().isBedfight()) {

                boolean bedGone = aTeam ? match.bedABroken : match.bedBBroken;

                if (profile.getMatch().kit.getGameRules().isBedfight()) {
                    if (!(player.getLocation().getY() >= match.getArena().getDeathZone()) && !match.getGamePlayer(player).isRespawned()) {
                        if (!bedGone) {

                            if (PlayerUtil.getLastAttacker(player) != null) {
                                PlayerUtil.getLastAttacker(player).playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
                            }
                            match.respawn(player);
                        } else {
                            profile.getMatch().onDeath(player);
                        }
                    }
                }
			} else if (profile.getMatch().kit.getGameRules().isBridge()) {
                
                if (!(player.getLocation().getY() >= match.getArena().getDeathZone()) && !match.getGamePlayer(player).isRespawned()) {
                    match.respawn(player);
                }
            } else if (profile.getMatch().kit.getGameRules().isSumo()) {
                
                if (!(player.getLocation().getY() >= match.getArena().getDeathZone()) && !match.getGamePlayer(player).isRespawned()) {
                    profile.getMatch().onDeath(player);
                }
            } else if (profile.getMatch().kit.getGameRules().isStickFight()) {
                
                if (!(player.getLocation().getY() >= match.getArena().getDeathZone()) && !match.getGamePlayer(player).isRespawned()) {
                    if (aTeam) {
                        if (match.playerALives > 1) {
                            match.playerALives--;
                            match.newRoundStickFight(player.getUniqueId());
                            match.newRoundStickFight(rival.getUniqueId());
                        } else {
                            profile.getMatch().onDeath(player);
                        }
				    } else if (!aTeam)
			            if (match.playerBLives > 1) {
                            match.playerBLives--;
                            match.newRoundStickFight(player.getUniqueId());
                            match.newRoundStickFight(rival.getUniqueId());
                        } else {
                            profile.getMatch().onDeath(player);
                        }
                    }
                }
        }
    }

    @EventHandler
    public void onBlockExplosion(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnition(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.FIREBALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onFireball(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getState() != ProfileState.FIGHTING) return;

        if (event.getItem() == null) return;
        if (!(event.getItem().getType() == Material.FIREBALL)) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            if (event.getItem().getAmount() == 1) {
                player.getInventory().remove(event.getItem());
            } else {
                event.getItem().setAmount(event.getItem().getAmount() - 1);
            }
            Fireball fireball = player.launchProjectile(Fireball.class);
            fireball.setIsIncendiary(false);
            fireball.setYield(2.0F);
            fireball.setVelocity(player.getLocation().getDirection().multiply(0.5)); // 0.5 (tested) 0.8 is good too
        }
    }

    @EventHandler
    private void onFireballHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getEntity();
            fireball.getWorld().createExplosion(fireball.getLocation(), 0.0F, false);
            fireball.getNearbyEntities(2.0, 2.0, 2.0).forEach(entity -> {
                if (entity instanceof Player) {
                    Player player = (Player) entity;

                    Vector direction = player.getLocation().getDirection().normalize();

                    double pitch = player.getLocation().getPitch();
                    double heightMultiplier = Math.sin(Math.toRadians(pitch)) * 1.8; // 2 (1.5 is good)
                    double speedMultiplier = player.isSprinting() ? 1.0 : 0.5;
                    double thresholdAngle = 70;

                    if (pitch < thresholdAngle) {
                        direction.multiply(-1);
                    }

                    Vector adjustedVelocity = direction.multiply(speedMultiplier).add(new Vector(0, heightMultiplier, 0));
                    Vector currentVelocity = player.getVelocity();
                    Vector newVelocity = currentVelocity.add(adjustedVelocity);

                    player.setVelocity(newVelocity);
                }
            });
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        Location loc = event.getEntity().getLocation();
        if (event.getEntity() instanceof Fireball) {
            for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
                for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
                    for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
                        Block block = Objects.requireNonNull(loc.getWorld()).getBlockAt(x, y, z);
                            if (block.getType() == Material.WOOL) {
                                block.setType(Material.AIR);
                            } else {
                                event.setCancelled(true);
                            }
                    }
                }
            }
            Location location = event.getLocation();
            List<Entity> nearbyEntities = (List<Entity>) Objects.requireNonNull(location.getWorld())
                .getNearbyEntities(location, 5, 5, 5);
            for (Entity entity : nearbyEntities) {
                if (entity instanceof Player) {
                    if (entity.getLocation().distance(event.getEntity().getLocation()) <= 5) {
                        entity.setVelocity(
                        entity.getLocation().subtract(event.getEntity().getLocation()).toVector().setY(0.5)
                            .normalize().multiply(1.25));
                    }
                }
            }
        } else if (event.getEntityType() == EntityType.PRIMED_TNT) {
            Iterator<Block> iterator = event.blockList().iterator();
            while (iterator.hasNext()) {
                Block block = iterator.next();
                Material type = block.getType();

                if (type != Material.WOOL && type != Material.WOOD && type != Material.ENDER_STONE) {
                    iterator.remove();
                }
            }
            for (Entity entity : event.getEntity().getNearbyEntities(5, 5, 5)) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    Vector direction = player.getLocation().toVector().subtract(event.getLocation().toVector()).normalize();
                    player.setVelocity(direction.multiply(1.5));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();

        if (profile.getMatch() != null) {
            Match match = profile.getMatch();
            Block block = event.getBlock();
            if (block.getType() == Material.TNT) {
                ItemStack itemStack = player.getItemInHand();
                itemStack.setAmount(itemStack.getAmount() - 1);
                player.setItemInHand(itemStack);

                TNTPrimed tntPrimed = event.getBlock().getLocation().getWorld().spawn(event.getBlock().getLocation().clone().add(0.5, 0.0, 0.5), TNTPrimed.class);
                tntPrimed.setYield((float) 4.0);
                tntPrimed.setFuseTicks(50);

                event.setCancelled(true);
                return;
            }
            /*if (event.getBlock().getType() == Material.TNT) {
                Block block = event.getBlock();

                TNTPrimed tnt = block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
                tnt.setFuseTicks(60); // TODO: add config for this.

                block.setType(Material.AIR);
                return;
            }*/
            if (match.getKit().getGameRules().isBuild() && match.getState() == MatchState.PLAYING_ROUND) {
                if (match.getKit().getGameRules().isSpleef()) {
                    event.setCancelled(true);
                    return;
                }

                Arena arena = match.getArena();
                Location blockLocation = event.getBlockPlaced().getLocation();
                int x = (int) event.getBlockPlaced().getLocation().getX();
                int y = (int) event.getBlockPlaced().getLocation().getY();
                int z = (int) event.getBlockPlaced().getLocation().getZ();
                Location newBlockLocation = new Location(arena.getWorld(), x, y, z);
                if (newBlockLocation.equals(new Location(arena.getSpawnA().getWorld(), (int) arena.getSpawnA().getX(), (int) arena.getSpawnA().getY(), (int) arena.getSpawnA().getZ()))
                        || newBlockLocation.equals(new Location(arena.getSpawnB().getWorld(), (int) arena.getSpawnB().getX(), (int) arena.getSpawnB().getY(), (int) arena.getSpawnB().getZ()))
                        || newBlockLocation.equals(new Location(arena.getSpawnA().getWorld(), (int) arena.getSpawnA().getX(), (int) arena.getSpawnA().getY() + 1, (int) arena.getSpawnA().getZ()))
                        || newBlockLocation.equals(new Location(arena.getSpawnB().getWorld(), (int) arena.getSpawnB().getX(), (int) arena.getSpawnB().getY() + 1, (int) arena.getSpawnB().getZ()))) {

                    event.getPlayer().sendMessage(CC.translate("&cYou can't place block blocks here!"));
                    event.setCancelled(true);
                    return;
                }

                if (y > arena.getMaxBuildHeight()) {
                    event.getPlayer().sendMessage(CC.RED + "You have reached the maximum build height.");
                    event.setCancelled(true);
                    return;
                }

                if (x >= arena.getX1() && x <= arena.getX2() && y >= arena.getY1() && y <= arena.getY2() &&
                        z >= arena.getZ1() && z <= arena.getZ2()) {
                    match.getPlacedBlocks().add(blockLocation);
                } else {
                    event.getPlayer().sendMessage(CC.RED + "You can't build outside of the arena!");
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
            if (match.getGamePlayer(event.getPlayer()).isRespawned()) {
                event.setCancelled(true);
            }
        } else {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

	@EventHandler
    public void onFallDamageEvent(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.getMatch() != null) {
            Match match = profile.getMatch();
            if (event.getBlock().getType().equals(Material.STAINED_CLAY)) {
                if (match.getKit().getGameRules().isBridge()) {
                    event.setCancelled(false);
                }
            }
            if (event.getBlock().getType() == Material.BED_BLOCK || event.getBlock().getType() == Material.BED) {
				Player player = event.getPlayer();
                Player rival = match.getOpponent(player);
                Profile rivalProfile = Profile.getByUuid(rival.getUniqueId());

                GameParticipant<MatchGamePlayer> participantA = match.getParticipantA();

				boolean aTeam = match.getParticipantA().containsPlayer(player.getUniqueId());

                Location spawn = aTeam ? match.getArena().getSpawnA() : match.getArena().getSpawnB();

                Location bed = event.getBlock().getLocation();
                if (bed.distanceSquared(spawn) > bed.distanceSquared(participantA != null && participantA.containsPlayer(match.getOpponentUUID(player.getUniqueId()).getUniqueId()) ?
                        match.getArena().getSpawnA() : match.getArena().getSpawnB())) {

                    if (aTeam) {
                        match.setBedBBroken(true);
                    } else {
                        match.setBedABroken(true);
                    }

                } else {
                    player.sendMessage(CC.translate("&cYou can't break your own bed!"));
                    event.setCancelled(true);
                    return;
                }

                if (!aTeam) {
                    //match.sendTitleA(CC.translate("&" + rivalProfile.getOptions().theme().getColor().getChar()) + "BED DESTROYED!", "&fYou will no longer respawn!", 40);
                    TitleAPI.sendBedDestroyed(rival);
                    event.getBlock().getDrops().clear();
                } else {
                    //match.sendTitleB(CC.translate("&" + rivalProfile.getOptions().theme().getColor().getChar()) + "BED DESTROYED!", "&fYou will no longer respawn!", 40);
                    TitleAPI.sendBedDestroyed(rival);
                    event.getBlock().getDrops().clear();
                }

                match.sendSound(Sound.ORB_PICKUP, 1.0F, 1.0F);
                match.sendSound(Sound.WITHER_DEATH, 1.0F, 1.0F);
                match.broadcast(" ");
                match.broadcast(Locale.MATCH_BED_BROKEN.format(player, aTeam ? CC.translate("&9Blue") : CC.translate("&cRed"),
                        aTeam ? CC.translate("&c" + player.getName()) : CC.translate("&9" + player.getName())));
                match.broadcast(" ");
            }

            if (match.getKit().getGameRules().isBuild() && match.getState() == MatchState.PLAYING_ROUND) {
                if (match.getKit().getGameRules().isSpleef()) {
                    if (event.getBlock().getType() == Material.SNOW_BLOCK ||
                        event.getBlock().getType() == Material.SNOW) {
                        match.getChangedBlocks().add(event.getBlock().getState());

                        event.getBlock().setType(Material.AIR);
                        event.getPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
                        event.getPlayer().updateInventory();
                    } else {
                        event.setCancelled(true);
                    }
                } else if (!match.getPlacedBlocks().remove(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
            if (match.kit.getGameRules().isBedfight() && (event.getBlock().getType().equals(Material.BED_BLOCK) || event.getBlock().getType().equals(Material.BED)) || event.getBlock().getType().equals(Material.ENDER_STONE) || event.getBlock().getType().equals(Material.WOOL) || (event.getBlock().getType().equals(Material.WOOD) && event.getBlock().getData() == 0)) {
                event.setCancelled(false);
            }
            if (match.getGamePlayer(event.getPlayer()).isRespawned()) {
                event.setCancelled(true);
            }
        } else {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Match match = profile.getMatch();
        Player player = event.getPlayer();
        if (!match.getGamePlayer(event.getPlayer()).isRespawned()) {
            if (match.getKit().getGameRules().isBridge() || match.getKit().getGameRules().isBattleRush()) { 
                if (player.getLocation().getBlock().getType() == Material.ENDER_PORTAL || player.getLocation().getBlock().getType() == Material.ENDER_PORTAL_FRAME) {
                    GameParticipant<MatchGamePlayer> participantA = match.getParticipantA();
				    boolean aTeam = match.getParticipantA().containsPlayer(player.getUniqueId());
                    Location spawn = aTeam ? match.getArena().getSpawnA() : match.getArena().getSpawnB();
                    Location playerLocation = player.getLocation();
                    if (playerLocation.distanceSquared(spawn) > playerLocation.distanceSquared(participantA != null && participantA.containsPlayer(match.getOpponentUUID(player.getUniqueId()).getUniqueId()) ?
                        match.getArena().getSpawnA() : match.getArena().getSpawnB())) {

                        if (aTeam) {
                            match.portalCountA++;
                            playerScoreManager(player, match.portalCountA);
                        } else {
                            match.portalCountB++;
                            playerScoreManager(player, match.portalCountB);
                        }
                    } else {
                        player.sendMessage(CC.translate("&cYou can't enter your own portal!"));
                        player.teleport(spawn);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

	@EventHandler
    public void onPlayerPickUpEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getMatch() != null && profile.getMatch().getState().equals(MatchState.ENDING_MATCH)) {
            event.setCancelled(true);
        }
    }

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.getMatch() != null) {
            Match match = profile.getMatch();

            if (match.getKit().getGameRules().isBuild() && match.getState() == MatchState.PLAYING_ROUND) {
                Arena arena = match.getArena();
                Block block = event.getBlockClicked().getRelative(event.getBlockFace());
                int x = (int) block.getLocation().getX();
                int y = (int) block.getLocation().getY();
                int z = (int) block.getLocation().getZ();

                if (y > arena.getMaxBuildHeight()) {
                    event.getPlayer().sendMessage(CC.RED + "You have reached the maximum build height.");
                    event.setCancelled(true);
                    return;
                }

                if (x >= arena.getX1() && x <= arena.getX2() && y >= arena.getY1() && y <= arena.getY2() &&
                        z >= arena.getZ1() && z <= arena.getZ2()) {
                    match.getPlacedBlocks().add(block.getLocation());
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Match match = profile.getMatch();

		if (profile.getState() == ProfileState.FIGHTING) {
			if (profile.getMatch().getGamePlayer(event.getPlayer()).isDead()) {
				event.setCancelled(true);
				return;
			}

			if (event.getItem().getItemStack().getType().name().contains("BOOK")) {
				event.setCancelled(true);
				return;
			}

            if (event.getItem().getItemStack().getType().equals(Material.ENDER_STONE) || (event.getItem().getItemStack().getType().equals(Material.WOOD) || event.getItem().getItemStack().getType().equals(Material.WOOL))) {
                event.setCancelled(false);
                return;
            }

			Iterator<Item> itemIterator = profile.getMatch().getDroppedItems().iterator();

			while (itemIterator.hasNext()) {
				Item item = itemIterator.next();

				if (item.equals(event.getItem())) {
					itemIterator.remove();
					return;
				}
			}

			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (event.getItemDrop().getItemStack().getType() == Material.BOOK ||
		    event.getItemDrop().getItemStack().getType() == Material.ENCHANTED_BOOK) {
			event.setCancelled(true);
			return;
		}

		if (profile.getState() == ProfileState.FIGHTING) {
			if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
				event.getItemDrop().remove();
				return;
			}

			if (event.getItemDrop().getItemStack().getType().name().contains("SWORD")) {
				event.setCancelled(true);
				return;
			}

			profile.getMatch().getDroppedItems().add(event.getItemDrop());
		}
	}

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        event.getDrops().clear();

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getMatch() != null) {

            Match match = profile.getMatch();

            boolean aTeam = match.getParticipantA().containsPlayer(player.getUniqueId());

            boolean bedGone = aTeam ? match.bedABroken : match.bedBBroken;

            if (profile.getMatch().getKit().getGameRules().isBedfight()) {
                event.getDrops().clear();
                if (!bedGone) {

                    if (PlayerUtil.getLastAttacker(player) != null) {
                        PlayerUtil.getLastAttacker(player).playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
                    }

                    match.respawn(player);
                } else {
                    profile.getMatch().onDeath(player);
                }
                return;
		    } else if (profile.getMatch().getKit().getGameRules().isBridge()) {
                event.getDrops().clear();
                match.fastRespawn(player);
                return;
            } else if (profile.getMatch().getKit().getGameRules().isStickFight()) {
                if (aTeam) {
                    if (match.playerALives > 0) {
                        match.playerALives--;
                        match.respawn(player);
                    } else {
                        match.onDeath(event.getEntity());
                    }
				} else if (!aTeam)
			        if (match.playerBLives > 0) {
                        match.playerBLives--;
                        match.respawn(player);
                    } else {
                        match.onDeath(event.getEntity());
                    }
                return;
            }

            match.onDeath(event.getEntity());
        }
    }

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(event.getPlayer().getLocation());
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();
			Profile profile = Profile.getByUuid(shooter.getUniqueId());

			if (profile.getState() == ProfileState.FIGHTING) {
				Match match = profile.getMatch();

				if (match.getState() == MatchState.STARTING_ROUND) {
					event.setCancelled(true);
				} else if (match.getState() == MatchState.PLAYING_ROUND) {
					if (event.getEntity() instanceof ThrownPotion) {
						match.getGamePlayer(shooter).incrementPotionsThrown();
					}
				}
			}
		}
	}

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            if (event.getEntity().getShooter() instanceof Player) {
                Player shooter = (Player) event.getEntity().getShooter();
                Profile shooterData = Profile.getByUuid(shooter.getUniqueId());
                Match match = shooterData.getMatch();

                if (shooterData.getState() == ProfileState.FIGHTING ) {
                    shooterData.getMatch().getGamePlayer(shooter).handleHit();
                    if (event.getEntityType() == EntityType.ARROW) { // Fixes some issues
                        event.getEntity().remove();
                    }
                    if (match.getKit().getGameRules().isBridge())
                    // Give the arrow back after 3 seconds
                    Bukkit.getScheduler().runTaskLater(Moon.get(), () -> {
                        if (match.getKit().getGameRules().isBridge()) {
                            if (!shooter.getInventory().contains(Material.ARROW)) {
                                PlayerUtil.giveArrowBack(shooter);
                            }
                        }
                    }, 60);
                }
            }
        }
    }

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPotionSplashEvent(PotionSplashEvent event) {
		if (event.getPotion().getShooter() instanceof Player) {
			Player shooter = (Player) event.getPotion().getShooter();
			Profile shooterData = Profile.getByUuid(shooter.getUniqueId());

			if (shooterData.getState() == ProfileState.FIGHTING &&
			    shooterData.getMatch().getState() == MatchState.PLAYING_ROUND) {
				if (event.getIntensity(shooter) <= 0.5D) {
					shooterData.getMatch().getGamePlayer(shooter).incrementPotionsMissed();
				}

				for (LivingEntity entity : event.getAffectedEntities()) {
					if (entity instanceof Player) {
						if (shooterData.getMatch().getGamePlayer((Player) entity) == null) {
							event.setIntensity((LivingEntity) entity, 0);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
				Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

				if (profile.getState() == ProfileState.FIGHTING && !profile.getMatch().getKit().getGameRules().isHealthRegeneration()) {
					event.setCancelled(true);
				}
			}
		}
	}

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getMatch() != null) {

                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {

                    Match match = profile.getMatch();
                    GameParticipant<MatchGamePlayer> participantA = match.getParticipant(player);

                    boolean bedGone = participantA != null && participantA.containsPlayer(event.getEntity().getUniqueId()) ? match.bedABroken : match.bedBBroken;
                    if (profile.getMatch().kit.getGameRules().isBedfight() && !bedGone) {
                        Location spawn = participantA != null && participantA.containsPlayer(event.getEntity().getUniqueId()) ?
                                match.getArena().getSpawnA() : match.getArena().getSpawnB();

                        //event.getEntity().teleport(spawn);
                        match.respawn(player);
                    } else if (profile.getMatch().kit.getGameRules().isBridge() || profile.getMatch().kit.getGameRules().isBattleRush()) {
                        Location spawn = participantA != null && participantA.containsPlayer(event.getEntity().getUniqueId()) ?
                                match.getArena().getSpawnA() : match.getArena().getSpawnB();
                        player.teleport(spawn);
                        match.fastRespawn(player);
                    } else if (profile.getMatch().kit.getGameRules().isBedfight() && bedGone) {
                        profile.getMatch().onDeath(player);
                    } else {
                        //profile.getMatch().onDeath(player);
                    }
                    return;
                }

                if (profile.getMatch().getState() != MatchState.PLAYING_ROUND) {
                    event.setCancelled(true);
                    return;
                }

                if (profile.getMatch().getGamePlayer(player).isDead()) {
                    event.setCancelled(true);
                    return;
                }

                if (profile.getMatch().getKit().getGameRules().isSumo() 
				       || profile.getMatch().getKit().getGameRules().isSpleef()
				       || profile.getMatch().getKit().getGameRules().isNoDamage()
                       || profile.getMatch().getKit().getGameRules().isStickFight()
			           || profile.getMatch().getKit().getGameRules().isBoxing()) {
                    event.setDamage(0);
                    player.setHealth(20.0);
                    player.updateInventory();
                }
            }
        }


    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        LivingEntity entity = event.getEntity();
        Vector dir = entity.getLocation().getDirection();
        Arrow arrow = (Arrow) event.getProjectile();
        double speed = arrow.getVelocity().length();
        Vector velocity = dir.multiply(speed);
        arrow.setVelocity(velocity);
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent event1) {
        Arrow arrow;
        Entity damager;
        Player player = event1.getPlayer();
        Vector velocity = event1.getVelocity();
        EntityDamageEvent event = player.getLastDamageCause();
        if (event != null && !event.isCancelled() && event instanceof EntityDamageByEntityEvent && (damager = ((EntityDamageByEntityEvent) event).getDamager()) instanceof Arrow && (arrow = (Arrow) damager).getShooter().equals(player)) {
            double speed = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
            Vector dir = arrow.getLocation().getDirection().normalize();
            double xVelocity = Moon.get().getSettingsConfig().getDouble("MATCH.BOW-BOOST.KNOCKBACK-VELOCITY.X");
            double yVelocity = Moon.get().getSettingsConfig().getDouble("MATCH.BOW-BOOST.KNOCKBACK-VELOCITY.Y");
            double zVelocity = Moon.get().getSettingsConfig().getDouble("MATCH.BOW-BOOST.KNOCKBACK-VELOCITY.Z");
            Vector newVelocity = new Vector((dir.getX() * speed * -1.0) * xVelocity, velocity.getY() * yVelocity, dir.getZ() * speed * zVelocity);

            event1.setVelocity(newVelocity);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (Moon.get().getSettingsConfig().getBoolean("MATCH.BOW-BOOST.MINIMIZE-DAMAGE")) {
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
                Player entity = (Player) event.getEntity();
                Player damager = (Player) ((Arrow)event.getDamager()).getShooter();
                if (entity == damager) {
                    event.setDamage(0);
                }
            }
        }
    }

	@EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getMatch() == null) return;
        if (profile.getMatch().getKit().getGameRules().isSumo()
                || profile.getMatch().getKit().getGameRules().isBedfight()
                || profile.getMatch().getKit().getGameRules().isSpleef()
				|| profile.getMatch().getKit().getGameRules().isSumo()
                || profile.getMatch().getKit().getGameRules().isBridge()
                || profile.getMatch().getKit().getGameRules().isBattleRush()
                || profile.getMatch().getKit().getGameRules().isStickFight()
                || profile.getMatch().getKit().getGameRules().isBoxing()) {
            event.setCancelled(true);
        }
    }

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDamageByEntityLow(EntityDamageByEntityEvent event) {
		Player attacker;

		if (event.getDamager() instanceof Player) {
			attacker = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
				attacker = (Player) ((Projectile) event.getDamager()).getShooter();
			} else {
				event.setCancelled(true);
				return;
			}
		} else {
			event.setCancelled(true);
			return;
		}

		if (attacker != null && event.getEntity() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			Profile damagedProfile = Profile.getByUuid(damaged.getUniqueId());
			Profile attackerProfile = Profile.getByUuid(attacker.getUniqueId());

			if (attackerProfile.getState() == ProfileState.SPECTATING || damagedProfile.getState() == ProfileState.SPECTATING) {
				event.setCancelled(true);
				return;
			}

			if (damagedProfile.getState() == ProfileState.FIGHTING && attackerProfile.getState() == ProfileState.FIGHTING) {
				Match match = attackerProfile.getMatch();

				if (!damagedProfile.getMatch().getMatchId().equals(attackerProfile.getMatch().getMatchId())) {
					event.setCancelled(true);
					return;
				}

				if (match.getGamePlayer(damaged).isDead()) {
					event.setCancelled(true);
					return;
				}

				if (match.getGamePlayer(attacker).isDead()) {
					event.setCancelled(true);
					return;
				}

				if (match.isOnSameTeam(damaged, attacker)) {
					event.setCancelled(true);
					return;
				}

				attackerProfile.getMatch().getGamePlayer(attacker).handleHit();
				damagedProfile.getMatch().getGamePlayer(damaged).resetCombo();

				if(match.getKit().getGameRules().isBoxing() && match.getState() != MatchState.STARTING_ROUND
                        && match.getState() != MatchState.ENDING_MATCH
                        && attackerProfile.getMatch().getGamePlayer(attacker).getHits() == 100){
                match.onDeath(damaged);
               }

			   if(match.getKit().getGameRules().isOnehit() && match.getState() != MatchState.STARTING_ROUND
                        && match.getState() != MatchState.ENDING_MATCH
                        && attackerProfile.getMatch().getGamePlayer(attacker).getHits() == 1){
                match.onDeath(damaged);
               }

				if (event.getDamager() instanceof Arrow) {
					int range = (int) Math.ceil(event.getEntity().getLocation().distance(attacker.getLocation()));
					double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0D;

					attacker.sendMessage(Locale.ARROW_DAMAGE_INDICATOR.format(
							range,
							damaged.getName(),
							health,
							StringEscapeUtils.unescapeJava("❤")
					));
				}
                if (event.getDamager().getType() == EntityType.PRIMED_TNT && event.getEntity() instanceof Player) {
                    event.setDamage(0);
                    event.setCancelled(true);
                }
				if (match.getGamePlayer(attacker).isRespawned()){
                    event.setCancelled(true);
                }
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player attacker = null;

            if (event.getDamager() instanceof Player) {
                attacker = (Player) event.getDamager();
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();

                if (projectile.getShooter() instanceof Player) {
                    attacker = (Player) projectile.getShooter();
                }
            }

            if (attacker != null) {
                PlayerUtil.setLastAttacker(victim, attacker);
            }
        }
    }

	@EventHandler
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
		if (event.getItem().getType() == Material.GOLDEN_APPLE) {
			if (event.getItem().hasItemMeta() &&
			    event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
				player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
			}
            if (profile.getMatch().getKit().getGameRules().isBridge()) {
                player.setFoodLevel(20);
                player.setSaturation(20);
                player.setHealth(20);
            }
		}
	}

    @EventHandler
    public void onPlayerItemConsumeEvent1(PlayerItemConsumeEvent event) {
        if (!event.getItem().getType().equals(Material.POTION)) return;
        if (!event.getItem().getType().equals(Material.GLASS_BOTTLE)) return;
        if (!Moon.get().getSettingsConfig().getBoolean("MATCH.REMOVE_POTION_BOTTLE")) return;

        TaskUtil.runLater(() -> event.getPlayer().setItemInHand(new ItemStack(Material.AIR)), 1L);
    }

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile.getState() == ProfileState.FIGHTING &&
			    profile.getMatch().getState() == MatchState.PLAYING_ROUND) {
				if (event.getFoodLevel() >= 20) {
					event.setFoodLevel(20);
					player.setSaturation(20);
				} else {
					event.setCancelled(ThreadLocalRandom.current().nextInt(100) > 25);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());

        if (profile.getState() == ProfileState.FIGHTING) {
			Match match = profile.getMatch();

			if (match.getState() == MatchState.STARTING_ROUND || match.getState() == MatchState.PLAYING_ROUND) {
				profile.getMatch().onDisconnect(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getState() == ProfileState.SPECTATING && event.getRightClicked() instanceof Player &&
		    player.getItemInHand() != null) {
			Player target = (Player) event.getRightClicked();

			if (Hotbar.fromItemStack(player.getItemInHand()) == HotbarItem.VIEW_INVENTORY) {
				new ViewInventoryMenu(target).openMenu(player);
			}
		}
	}

    @EventHandler
    public void onPearlLaunch(ProjectileLaunchEvent event) {
        Player player = (Player) event.getEntity().getShooter();

        Profile profile = Profile.getByUuid(player.getUniqueId());
        Match match = profile.getMatch();

        // If match doesn't exist then stop.
        if (match == null) {
            return;
        }

        if (match.getState() != MatchState.PLAYING_ROUND) {
            if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof EnderPearl) {
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                event.setCancelled(true);
                return;
            }
        }

        // Set pearl cooldown to player.
        if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof EnderPearl && profile.getState() == ProfileState.FIGHTING || event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof EnderPearl) {
            if (profile.getEnderpearlCooldown().hasExpired()) {
                //if(!profile.getMatch().getKit().getGameRules().isPearlfight()) {
                    profile.setEnderpearlCooldown(new Cooldown(16_000));
                //}
            } else {
                String time = TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
                player.sendMessage(Locale.MATCH_ENDERPEARL_COOLDOWN.format(time,
                    (time.equalsIgnoreCase("1.0") ? "" : "s")));
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                event.setCancelled(true);
                return;
            }
        }
    }


	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = event.getItem();

		if (itemStack != null && (event.getAction() == Action.RIGHT_CLICK_AIR ||
		                          event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile.getState() == ProfileState.FIGHTING) {
				Match match = profile.getMatch();

				if (Hotbar.fromItemStack(itemStack) == HotbarItem.SPECTATE_STOP) {
					match.onDisconnect(player);
					return;
				}

				if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
					ItemStack kitItem = Hotbar.getItems().get(HotbarItem.KIT_SELECTION);

					if (itemStack.getType() == kitItem.getType() &&
					    itemStack.getDurability() == kitItem.getDurability()) {
						Matcher matcher = HotbarItem.KIT_SELECTION.getPattern().
								matcher(itemStack.getItemMeta().getDisplayName());

						if (matcher.find()) {
							String kitName = matcher.group(2);
							KitLoadout kitLoadout = null;

							if (kitName.equals("Default")) {
								kitLoadout = match.getKit().getKitLoadout();
							} else {
								for (KitLoadout find : profile.getKitData().get(match.getKit()).getLoadouts()) {
									if (find != null && find.getCustomName().equals(kitName)) {
										kitLoadout = find;
									}
								}
							}

							if (kitLoadout != null) {
								player.sendMessage(Locale.MATCH_GIVE_KIT.format(kitLoadout.getCustomName()));
								//player.getInventory().setArmorContents(kitLoadout.getArmor());
								//player.getInventory().setContents(kitLoadout.getContents());
                                if (match.getKit().getGameRules().isBedfight()) {
                                    player.getInventory().setContents(kitLoadout.getContents());
                                    KitUtils.giveBedFightKit(player);
                                } else if (match.getKit().getGameRules().isBridge() || match.getKit().getGameRules().isBattleRush()) {
                                    player.getInventory().setContents(kitLoadout.getContents());
                                    KitUtils.giveBridgeKit(player);
                                } else if (match.getKit().getGameRules().isStickFight()) {
                                    player.getInventory().setContents(kitLoadout.getContents());
                                    KitUtils.giveLivesKit(player);
                                } else {
                                    player.getInventory().setArmorContents(kitLoadout.getArmor());
                                    player.getInventory().setContents(kitLoadout.getContents());
                                }
								player.updateInventory();
								event.setCancelled(true);
								return;
							}
						}
					}
				}
			}
		}
	}

	private void playerScoreManager(Player player, int portalCount) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Match match = profile.getMatch();
        Player rival = match.getOpponent(player);
        Profile rivalProfile = Profile.getByUuid(rival.getUniqueId());
        String playerTheme = "&" + profile.getOptions().theme().getColor().getChar();
        String rivalTheme = "&" + rivalProfile.getOptions().theme().getColor().getChar();
		boolean aTeam = match.getParticipantA().containsPlayer(player.getUniqueId());
        boolean isRivalATeam = match.getParticipantA().containsPlayer(rival.getUniqueId());
		int playerPortalCount = aTeam ? match.portalCountA : match.portalCountB;
        boolean playerPortal = playerPortalCount >= 5;
        if (match.getKit().getGameRules().isBridge()) {
		    playerPortal = playerPortalCount >= 5;
        } else if (match.getKit().getGameRules().isBattleRush()) {
            playerPortal = playerPortalCount >= 3;
        }
		MatchGamePlayer gamePlayer = profile.getMatch().getGamePlayer(player);
		Location spawn = aTeam ? match.getArena().getSpawnA() : match.getArena().getSpawnB();
        if (playerPortal) {
            player.teleport(spawn);
            match.sendSound(Sound.WITHER_DEATH, 1.0F, 1.0F);
			match.onDeath(rival);
        } else {
            match.roundNumberBridge++;
            int roundNumber = match.roundNumberBridge + 1;
            if (isRivalATeam) {
                rival.sendMessage(CC.translate(" " + rivalTheme + "&lStarting Round #" + roundNumber));
                rival.sendMessage(CC.translate("  &7■ &fYour Points: " + rivalTheme + match.portalCountA));
                rival.sendMessage(CC.translate("  &7■ &fTheir Points: " + rivalTheme + match.portalCountB));
            } else {
                rival.sendMessage(CC.translate(" " + rivalTheme + "&lStarting Round #" + roundNumber));
                rival.sendMessage(CC.translate("  &7■ &fYour Points: " + rivalTheme + match.portalCountB));
                rival.sendMessage(CC.translate("  &7■ &fTheir Points: " + rivalTheme + match.portalCountA));
            }
            if (aTeam) {
                player.sendMessage(CC.translate(" " + playerTheme + "&lStarting Round #" + roundNumber));
                player.sendMessage(CC.translate("  &7■ &fYour Points: " + playerTheme + match.portalCountA));
                player.sendMessage(CC.translate("  &7■ &fTheir Points: " + playerTheme + match.portalCountB));
            } else {
                player.sendMessage(CC.translate(" " + playerTheme + "&lStarting Round #" + roundNumber));
                player.sendMessage(CC.translate("  &7■ &fYour Points: " + playerTheme + match.portalCountB));
                player.sendMessage(CC.translate("  &7■ &fTheir Points: " + playerTheme + match.portalCountA));
            }
            String playerColor = aTeam ? "&c&l" : "&9&l";
            match.sendTitleA(CC.translate(playerColor + player.getName() + " &fhas scored."), "", 40);
            match.sendTitleB(CC.translate(playerColor + player.getName() + " &fhas scored."), "", 40);
            match.newRound(player.getUniqueId());
            match.newRound(rival.getUniqueId());
        }
    }

}
