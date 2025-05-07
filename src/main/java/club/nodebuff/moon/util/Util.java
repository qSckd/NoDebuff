package club.nodebuff.moon.util;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Util {
    public static boolean isNull(String str) {
        return str == null || str.equalsIgnoreCase("null") || str.equalsIgnoreCase("");
    }

    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    public static void damage(Player player, double damage) {
        EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.CUSTOM, damage);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            player.damage(damage);
        }

    }

    public static void performCommand(Player player, String command) {
        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, "/" + command);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            player.performCommand(command);
        }
    }

    public static void setBlockFast(final Location location, final Material material, final boolean applyPhysics) {
        setBlockFast(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), material.getId(), (byte) 0, applyPhysics);
    }
    public static void setBlockFast(final org.bukkit.World world, final int x, final int y, final int z, final int blockId, final byte data, final boolean applyPhysics) {
        try {
            final net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
            final net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
            final BlockPosition bp = new BlockPosition(x, y, z);
            final int combined = blockId + (data << 12);
            final IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(combined);
            w.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
            chunk.a(bp, ibd);
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static Location getBedBlockNearBy(Location location) {
        Location bedLocation2 = location.clone();

        if (location.clone().add(1,0,0).getBlock().getType() == Material.BED_BLOCK) {
            return location.clone().add(1,0,0);
        }if (location.clone().add(-1,0,0).getBlock().getType() == Material.BED_BLOCK) {
            return location.clone().add(-1,0,0);
        }if (location.clone().add(0,0,1).getBlock().getType() == Material.BED_BLOCK) {
            return location.clone().add(0,0,1);
        }if (location.clone().add(0,0,-1).getBlock().getType() == Material.BED_BLOCK) {
            return location.clone().add(0,0,-1);
        }
        return bedLocation2;
    }


    public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        Collection<Class<?>> classes = new ArrayList<>();

        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar!.*", ".jar").replaceFirst("file:", "");
        JarFile jarFile;

        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw (new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e));
        }

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;

            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }

            if (className != null) {
                Class<?> clazz = null;

                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }

        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (ImmutableSet.copyOf(classes));
    }

    //Credit: https://github.com/lulu2002/DatouNms/blob/master/src/main/java/me/lulu/datounms/v1_8_R3/CraftCommonNMS.java
    public static void playDeathAnimation(Player player, List<Player> viewers) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = (( CraftWorld ) player.getWorld()).getHandle();
        CraftPlayer cp = (CraftPlayer) player;
        EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, cp.getProfile(), new PlayerInteractManager(nmsWorld));
        npc.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        PacketPlayOutPlayerInfo removeRealPlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, cp.getHandle());
        PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc);
        PacketPlayOutPlayerInfo removePlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc);
        PacketPlayOutPlayerInfo addRealPlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, cp.getHandle());
        PacketPlayOutNamedEntitySpawn entitySpawn = new PacketPlayOutNamedEntitySpawn(npc);
        PacketPlayOutEntityStatus entityDeath = new PacketPlayOutEntityStatus(npc, ( byte ) 3);

        for (Player o : viewers) {
            PlayerConnection connection = ((CraftPlayer) o).getHandle().playerConnection;
            connection.sendPacket(removeRealPlayer);
            connection.sendPacket(addPlayer);
            connection.sendPacket(entitySpawn);
            connection.sendPacket(entityDeath);
        }

        TaskUtil.runLaterAsync(()-> {
            for (Player o : viewers) {
                if (o.isOnline()) {
                    PlayerConnection connection = ((CraftPlayer) o).getHandle().playerConnection;

                    connection.sendPacket(removePlayer);
                    if (cp.isOnline()) {
                        connection.sendPacket(addRealPlayer);
                    }
                }
            }
        }, 2L);
    }

    private static void randomLocationWithinBlock(Location loc, double xs, double ys, double zs) {
        double prevX = loc.getX();
        double prevY = loc.getY();
        double prevZ = loc.getZ();
        loc.add(xs, ys, zs);
        if (loc.getX() < Math.floor(prevX)) {
            loc.setX(Math.floor(prevX));
        }
        if (loc.getX() >= Math.ceil(prevX)) {
            loc.setX(Math.ceil(prevX - 0.01));
        }
        if (loc.getY() < Math.floor(prevY)) {
            loc.setY(Math.floor(prevY));
        }
        if (loc.getY() >= Math.ceil(prevY)) {
            loc.setY(Math.ceil(prevY - 0.01));
        }
        if (loc.getZ() < Math.floor(prevZ)) {
            loc.setZ(Math.floor(prevZ));
        }
        if (loc.getZ() >= Math.ceil(prevZ)) {
            loc.setZ(Math.ceil(prevZ - 0.01));
        }
    }

    public static void setSource(final TNTPrimed tntPrimed, final Player player) {
        EntityLiving handle = ((CraftLivingEntity)player).getHandle();
        EntityTNTPrimed handle2 = ((CraftTNTPrimed)tntPrimed).getHandle();
        try {
            Field declaredField = EntityTNTPrimed.class.getDeclaredField("source");
            declaredField.setAccessible(true);
            declaredField.set(handle2, handle);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void pushAway(Player player, Location l, double hf, double rf) {
        final Location loc = player.getLocation();

        double hf1 = Math.max(-4, Math.min(4, hf));
        double rf1 = Math.max(-4, Math.min(4, -1 * rf));

        player.setVelocity(l.toVector().subtract(loc.toVector()).normalize().multiply(rf1).setY(hf1));
    }

    public static List<Block> getBlocksAroundCenter(Location loc, int radius) {
        List<Block> blocks = new ArrayList<>();

        for (int x = (loc.getBlockX() - radius); x <= (loc.getBlockX() + radius); x++) {
            for (int y = (loc.getBlockY() - radius); y <= (loc.getBlockY() + radius); y++) {
                for (int z = (loc.getBlockZ() - radius); z <= (loc.getBlockZ() + radius); z++) {
                    Location l = new Location(loc.getWorld(), x, y, z);
                    if (l.distance(loc) <= radius) {
                        blocks.add(l.getBlock());
                    }
                }
            }
        }

        return blocks;
    }
}
