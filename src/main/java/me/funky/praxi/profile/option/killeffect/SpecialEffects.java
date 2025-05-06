
package club.nodebuff.moon.profile.option.killeffect;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.profile.Profile;
import club.nodebuff.moon.util.LightningUtil;
import club.nodebuff.moon.util.TaskUtil;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;

@Getter
public enum SpecialEffects {
    NONE("None", Material.RECORD_11, "", (player, watchers) -> {}),
    LIGHTNING("Lightning", Material.BEACON, "", (player, watchers) -> {
        try {
            Arrays.asList(watchers).forEach(watcher -> {
                TaskUtil.runAsync(new BukkitRunnable() {
                    @Override
                    public void run() {
                        LightningUtil.spawnLightning(watcher, player.getLocation());
                    }
                });
            });
        } catch (Exception ignored) {
        }
    }, 0),
    BLOOD("Blood", Material.REDSTONE, "blood", (player, watchers) -> {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.BLOCK_CRACK, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.2f, 0.2f, 0.2f, 1.0f, 20, Material.REDSTONE_BLOCK.getId());
        for (Player watcher : watchers) {
            for (int i = 0; i < 5; ++i) {
                ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(packet);
            }
            watcher.playSound(player.getLocation(), Sound.FALL_BIG, 1.0f, 0.5f);
        }
    }, 1500),
    EXPLOSION("Explosion", Material.TNT, "explosion", (player, watchers) -> {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_HUGE, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.2f, 0.2f, 0.2f, 1.0f, 20);
        for (Player watcher : watchers) {
            ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(packet);
            watcher.playSound(player.getLocation(), Sound.EXPLODE, 1.0f, 0.7f);
        }
    }, 1500),
    FLAME("Flame", Material.BLAZE_POWDER, "flame", (player, watchers) -> {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.5f, 0.5f, 0.5f, 0.1f, 20, new int[0]);
        for (Player watcher : watchers) {
            ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(packet);
        }
    }, 1500),
    PINATA("Pinata", Material.STICK, "pinata", (player, watchers) -> {
        byte[] colors = new byte[]{1, 2, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15};
        for (int n : colors) {
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.ITEM_CRACK, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.0f, 0.0f, 0.0f, 0.5f, 10, new int[]{Material.INK_SACK.getId(), n});
            for (Player watcher : watchers) {
                ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(packet);
                watcher.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1.0f, 0.7f);
            }
        }
    }, 2000),
    SHATTERED("Shattered", Material.ANVIL, "shattered", (player, watchers) -> {
        byte[] grayscale = new byte[]{0, 7, 8, 15};
        for (int n : grayscale) {
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.ITEM_CRACK, false, (float)player.getLocation().getX(), (float)player.getLocation().add(0.0, 1.0, 0.0).getY(), (float)player.getLocation().getZ(), 0.0f, 0.0f, 0.0f, 0.5f, 20, new int[]{Material.STAINED_GLASS.getId(), n});
            for (Player watcher : watchers) {
                ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(packet);
                watcher.playSound(player.getLocation(), Sound.GLASS, 1.0f, 0.55f);
            }
        }
    }, 2000),
    SHOCKWAVE("Shockwave", Material.FIREWORK_CHARGE, "shockwave", (player, watchers) -> {
        final Location loc = player.getLocation().clone();
        Material block = loc.getBlock().getRelative(BlockFace.DOWN).getType();
        if (block == Material.AIR) {
            block = Material.ICE;
        }
        final Material finalBlock = block;
        new BukkitRunnable(){
            int i = 0;
            double radius = 0.5;

            public void run() {
                this.radius += 0.5;
                for (double t = 0.0; t < 50.0; t += 1.5) {
                    double x = this.radius * Math.sin(t);
                    double z = this.radius * Math.cos(t);
                    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.BLOCK_CRACK, false, (float)(loc.getX() + x), (float)loc.getY(), (float)(loc.getZ() + z), 0.0f, 0.0f, 0.0f, 1.0f, 6, new int[]{finalBlock.getId()});
                    for (Player watcher : watchers) {
                        ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(packet);
                        watcher.playSound(loc, Sound.DIG_GRAVEL, 0.3f, 0.45f);
                    }
                }
                ++this.i;
                if (this.i >= 4) {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(Moon.get(), 0L, 5L);
    }, 2000),
    WISDOM("Wisdom", Material.BOOK, "wisdom", (player, watchers) -> {
        Location loc = player.getLocation().clone().add(0.0, 2.8, 0.0);
        for (double d = 0.0; d < Math.PI * 2; d += 0.5235987755982988) {
            double x = Math.sin(d);
            double z = Math.cos(d);
            Vector v = new Vector(x, -0.5, z).multiply(1.5);
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.ENCHANTMENT_TABLE, false, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), (float)v.getX(), (float)v.getY(), (float)v.getZ(), 0.7f, 0, new int[0]);
            PacketPlayOutWorldParticles books = new PacketPlayOutWorldParticles(EnumParticle.ITEM_CRACK, false, (float)player.getLocation().getX(), (float)player.getLocation().getY(), (float)player.getLocation().getZ(), 0.0f, 0.0f, 0.0f, 0.5f, 1, new int[]{Material.BOOK.getId(), 0});
            for (Player watcher : watchers) {
                for (int i = 0; i < 5; ++i) {
                    ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(packet);
                }
                ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(books);
                watcher.playSound(player.getLocation(), Sound.IRONGOLEM_DEATH, 0.3f, 0.4f);
            }
        }
    }, 2000),
    SOUL("Soul", Material.MAGMA_CREAM, "soul", (player, watchers) -> new BukkitRunnable(){
        final Location loc;
        double t;
        final double r;
        {
            this.loc = player.getLocation();
            this.t = 0.0;
            this.r = 0.75;
        }

        public void run() {
            this.t += 0.3141592653589793;
            double x = 0.75 * Math.cos(this.t);
            double y = 0.25 * this.t;
            double z = 0.75 * Math.sin(this.t);
            this.loc.add(x, y, z);
            for (Player watcher : watchers) {
                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.VILLAGER_HAPPY, false, (float)this.loc.getX(), (float)this.loc.getY(), (float)this.loc.getZ(), 0.0f, 0.0f, 0.0f, 1.0f, 0, new int[0]);
                ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(packet);
                if (this.t > Math.PI * 3) {
                    PacketPlayOutWorldParticles pop = new PacketPlayOutWorldParticles(EnumParticle.HEART, false, (float)this.loc.getX(), (float)this.loc.getY(), (float)this.loc.getZ(), 0.0f, 0.0f, 0.0f, 1.0f, 0, new int[0]);
                    ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(pop);
                    Bukkit.getScheduler().runTaskLater(Moon.get(), () -> watcher.playSound(this.loc, Sound.NOTE_PLING, 1.0f, 1.6f), 1L);
                    Bukkit.getScheduler().runTaskLater(Moon.get(), () -> watcher.playSound(this.loc, Sound.NOTE_PLING, 1.0f, 1.7f), 3L);
                    Bukkit.getScheduler().runTaskLater(Moon.get(), () -> watcher.playSound(this.loc, Sound.NOTE_PLING, 1.0f, 1.8f), 5L);
                    Bukkit.getScheduler().runTaskLater(Moon.get(), () -> watcher.playSound(this.loc, Sound.NOTE_PLING, 1.0f, 1.9f), 7L);
                    Bukkit.getScheduler().runTaskLater(Moon.get(), () -> watcher.playSound(this.loc, Sound.NOTE_PLING, 1.0f, 2.0f), 9L);
                    this.cancel();
                    continue;
                }
                this.loc.subtract(x, y, z);
            }
        }
    }.runTaskTimerAsynchronously(Moon.get(), 0L, 1L), 2000);

    private final String name;
    private final Material icon;
    private final String permission;
    private final EffectCallable callable;
    private int price;

    SpecialEffects(String name, Material icon, String permission, EffectCallable callable) {
        this.name = name;
        this.icon = icon;
        this.permission = permission;
        this.callable = callable;
        this.price = 0;
    }

    SpecialEffects(String name, Material icon, String permission, EffectCallable callable, int price) {
        this.name = name;
        this.icon = icon;
        this.permission = permission;
        this.callable = callable;
        this.price = price;
    }

    public static SpecialEffects getByName(String input) {
        for (SpecialEffects type : SpecialEffects.values()) {
            if (!type.name().equalsIgnoreCase(input) && !type.getName().equalsIgnoreCase(input)) continue;
            return type;
        }
        return null;
    }

    public void setPrice(int p) {
        this.price = p;
    }

    public boolean hasPermission(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        //return player.hasPermission(this.permission) || this.permission.isEmpty();
        return profile.hasPermission(this.permission) || this.permission.isEmpty();
    }

    private static PacketPlayOutWorldParticles createParticlePacket(EnumParticle particle, Location location) {
        float x = (float) location.getX();
        float y = (float) location.getY();
        float z = (float) location.getZ();

        return new PacketPlayOutWorldParticles(particle, false, x, y, z, 0.5f, 0.5f, 0.5f, 0.1f, 10);
    }

}
