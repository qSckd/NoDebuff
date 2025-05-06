package club.nodebuff.moon.util;

import club.nodebuff.moon.Moon;
import org.bukkit.*;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;

import java.util.List;
import java.util.UUID;

public class PlayerUtil {

	public static Player setLastAttacker(Player victim, Player attacker) {
        if (attacker == null) {
            victim.setMetadata("lastAttacker", new FixedMetadataValue(Moon.get(), null));
			return null;
        } else {
            victim.setMetadata("lastAttacker", new FixedMetadataValue(Moon.get(), attacker.getUniqueId()));
			return attacker;
        }
    }

	public static Player getLastAttacker(Player victim) {
        if (victim.hasMetadata("lastAttacker")) {
            return Bukkit.getPlayer((UUID) victim.getMetadata("lastAttacker").get(0).value());
        } else {
            return null;
        }
    }

	public static boolean setImmune(Player player, int ticks) {
        return player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, ticks, 250));
    }

	public static void reset(Player player) {
		reset(player, true);
	}

	public static void sendTitle(Player player, String header, String footer, int duration) {
        player.sendTitle(new Title(CC.translate(header), CC.translate(footer), 1, duration, 10));
    }

	public static Boolean inReplay(Player player) {
        if (player.hasMetadata("inReplay")) {
            return player.getMetadata("inReplay").get(0).value().equals(true);
        }
        return false;
    }

	public static void reset(Player player, boolean resetHeldSlot) {
		if (!player.hasMetadata("frozen")) {
			player.setWalkSpeed(0.2F);
			player.setFlySpeed(0.1F);
		}

		player.setHealth(20.0D);
		player.setSaturation(20.0F);
		player.setFallDistance(0.0F);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setMaximumNoDamageTicks(20);
		player.setExp(0.0F);
		player.setLevel(0);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);
		player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

		if (resetHeldSlot) {
			player.getInventory().setHeldItemSlot(0);
		}

		player.updateInventory();
	}

	public static void doVelocityChange(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public static void giveArrowBack(Player player) {
        if (player.getInventory().getItem(35) != null) {
            player.getInventory().addItem(new ItemStack(Material.ARROW));
        } else {
            player.getInventory().setItem(35, new ItemStack(Material.ARROW));
        }
        player.updateInventory();
    }

    public static void enableSpectator(Player player) {
        //player.setGameMode(GameMode.SPECTATOR);
        //a
        player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(true);
		player.setFlying(true);

		player.setAllowFlight(true);
		player.setFlying(true);
		player.spigot().setCollidesWithEntities(false);
        //a
    }

	public static void denyMovement(Player player) {
        if (player == null) {
            return;
        }
        player.setFlying(false);
        player.setWalkSpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
    }

	public static void allowMovement(Player player) {
        if (player == null) {
            return;
        }
        player.setFlying(false);
        player.setWalkSpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

	public static ItemStack getPlayerHead(UUID playerUUID) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwner(Bukkit.getPlayer(playerUUID).getName());
        head.setItemMeta(skullMeta);
        return head;
    }

	public static int getPing(Player player) {
		try {
			return ((CraftPlayer)player).getHandle().ping;
		} catch (Exception ex) {
			return 0;
		}
	}

    public static void playSimpleParticleEffect(Location location, Effect effect) {
        location.getWorld().playEffect(location, effect, effect.getData());
    }

}
