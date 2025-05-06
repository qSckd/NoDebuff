package me.funky.praxi.arena;

import lombok.Getter;
import lombok.Setter;
import me.funky.praxi.Praxi;
import me.funky.praxi.arena.impl.SharedArena;
import me.funky.praxi.arena.impl.StandaloneArena;
import me.funky.praxi.arena.cuboid.Cuboid;
import me.funky.praxi.arena.cache.ArenaCache;
import me.funky.praxi.arena.cache.ArenaChunk;
import me.funky.praxi.kit.Kit;
import me.funky.praxi.util.ChunkUtil;
import me.funky.praxi.util.LocationUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.Location;
import net.minecraft.server.v1_8_R3.Chunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;

@Getter
@Setter
public class Arena extends Cuboid {

    @Getter
    private static final List<Arena> arenas = new ArrayList<>();

    protected String name;
    protected String displayName;
    protected Location location1, location2, spawnA, spawnB;
    protected boolean active, duplicate;
    private ArenaCache cache;
    private List<String> kits = new ArrayList<>();
    private Map<Chunk, ChunkSnapshot> chunkSnapshots = new HashMap<>();

    public Arena(String name, Location location1, Location location2) {
        super(location1, location2);
        this.name = name;
        this.displayName = name + "#0";
    }

    public Arena(String name, Location location1, Location location2, Location spawnA, Location spawnB) {
        super(location1, location2);
        this.name = name;
        this.spawnA = spawnA;
        this.spawnB = spawnB;
        this.location1 = location1;
        this.location2 = location2;
        this.displayName = name + "#0";
    }

    /**
     * Loads all arenas from the arenas configuration file.
     *
     * <p>This method is called when the plugin is enabled and is responsible for loading all arenas from the arenas
     * configuration file. It iterates through all {@link ArenaType}s and creates a new {@link Arena} instance for each
     * of them. It also sets the arena's display name, spawn points, and kits if they are specified in the configuration
     * file. If the arena is a {@link StandaloneArena}, it also loads all of its duplicates.
     */
    public static void init() {
        FileConfiguration configuration = Praxi.get().getArenasConfig().getConfiguration();

        if (configuration.contains("arenas")) {
            for (String arenaName : configuration.getConfigurationSection("arenas").getKeys(false)) {
                String path = "arenas." + arenaName;

                ArenaType arenaType = ArenaType.valueOf(configuration.getString(path + ".type"));
                Location location1 = LocationUtil.deserialize(configuration.getString(path + ".cuboid.location1"));
                Location location2 = LocationUtil.deserialize(configuration.getString(path + ".cuboid.location2"));
                Location spawnA = LocationUtil.deserialize(configuration.getString(path + ".spawnA"));
                Location spawnB = LocationUtil.deserialize(configuration.getString(path + ".spawnB"));
                Arena arena;

                if (arenaType == ArenaType.STANDALONE) {
                    arena = new StandaloneArena(arenaName, location1, location2);
                } else if (arenaType == ArenaType.SHARED) {
                    arena = new SharedArena(arenaName, location1, location2);
                } else {
                    continue;
                }

                if (configuration.contains(path + ".cuboid.location1")) {
                    arena.setLocation1(LocationUtil.deserialize(configuration.getString(path + ".cuboid.location1")));
                }

                if (configuration.contains(path + ".cuboid.location2")) {
                    arena.setLocation2(LocationUtil.deserialize(configuration.getString(path + ".cuboid.location2")));
                }

                if (configuration.contains(path + ".spawnA")) {
                    arena.setSpawnA(LocationUtil.deserialize(configuration.getString(path + ".spawnA")));
                }

                if (configuration.contains(path + ".spawnB")) {
                    arena.setSpawnB(LocationUtil.deserialize(configuration.getString(path + ".spawnB")));
                }


                String displayName = configuration.getString(path + ".displayName");
                arena.setDisplayName(displayName + "#0");

                if (configuration.contains(path + ".kits")) {
                    for (String kitName : configuration.getStringList(path + ".kits")) {
                        arena.getKits().add(kitName);
                    }
                }

                if (arena instanceof StandaloneArena && configuration.contains(path + ".duplicates")) {
                    for (String duplicateId : configuration.getConfigurationSection(path + ".duplicates").getKeys(false)) {
                        location1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".cuboid.location1"));
                        location2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".cuboid.location2"));
                        spawnA = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawnA"));
                        spawnB = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawnB"));

                        Arena duplicate = new Arena(arenaName, location1, location2, spawnA, spawnB);

                        duplicate.setDisplayName(arena.getName() + "#" +duplicateId);
                        duplicate.setSpawnA(spawnA);
                        duplicate.setSpawnB(spawnB);
                        duplicate.setLocation1(location1);
                        duplicate.setLocation2(location2);
                        duplicate.setKits(arena.getKits());

                        ((StandaloneArena) arena).getDuplicates().add(duplicate);

                        Arena.getArenas().add(duplicate);
                    }
                }

                Arena.getArenas().add(arena);
            }
        }
    }

    /**
     * Finds an arena by name.
     *
     * @param name the name of the arena
     * @return the arena found, or null if none found
     */
    public static Arena getByName(String name) {
        for (Arena arena : arenas) {
            if (arena.getType() != ArenaType.DUPLICATE && arena.getName() != null &&
                    arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }

        return null;
    }

    /**
     * Retrieves a random arena that is compatible with the specified kit.
     * <p>
     * This method filters through the available arenas to find those that are set up
     * and compatible with the provided kit's requirements. It then selects and returns
     * a random arena from the filtered list. If no compatible arenas are found, it returns null.
     * 
     * @param kit the kit for which to find a compatible arena
     * @return a random arena that is compatible with the specified kit, or null if none are found
     */
    public static Arena getRandomArena(Kit kit) {
        List<Arena> _arenas = new ArrayList<>();

        for (Arena arena : arenas) {
            if (!arena.isSetup()) {
                continue;
            }

            if (!arena.getKits().contains(kit.getName())) {
                continue;
            }

            if (kit.getGameRules().isBuild() && !arena.isActive() && (arena.getType() == ArenaType.STANDALONE ||
                    arena.getType() == ArenaType.DUPLICATE)) {
                _arenas.add(arena);
            } else if (!kit.getGameRules().isBuild() && arena.getType() == ArenaType.SHARED) {
                _arenas.add(arena);
            }
        }

        if (_arenas.isEmpty()) {
            return null;
        }

        return _arenas.get(ThreadLocalRandom.current().nextInt(_arenas.size()));
    }
    /**
     * Returns the location of spawn A, or null if it is not set.
     *
     * @return the location of spawn A, or null
     */
    public Location getSpawnA() {
        if (spawnA == null) {
            return null;
        }

        return spawnA.clone();
    }

    /**
     * Returns the location of spawn B, or null if it is not set.
     *
     * @return the location of spawn B, or null
     */
    public Location getSpawnB() {
        if (spawnB == null) {
            return null;
        }

        return spawnB.clone();
    }

    /**
     * Takes a snapshot of the arena's chunks. This method is used to create a temporary backup of the arena
     * state before a match starts. The snapshot can then be restored with {@link #restoreSnapshot()} after a match
     * has finished.
     * 
     * @see #restoreSnapshot()
     */
    public void takeSnapshot() {
        Cuboid cuboid = new Cuboid(location1, location2);
        ArenaCache chunkCache = new ArenaCache();
        cuboid.getChunks().forEach(chunk -> {
            chunk.load();
            Chunk nmsChunk = ((CraftChunk)chunk).getHandle();
            ChunkSection[] nmsSections = ChunkUtil.copyChunkSections(nmsChunk.getSections());
            chunkCache.chunks.put(new ArenaChunk(chunk.getX(), chunk.getZ()), ChunkUtil.copyChunkSections(nmsSections));
        });

        this.cache = chunkCache;
    }

    /**
     * Restores the arena snapshot that was taken with {@link #takeSnapshot()}.
     * This method is used to restore the arena state after a match has finished.
     * 
     * @see #takeSnapshot()
     */
    public void restoreSnapshot() {
        Cuboid cuboid = new Cuboid(location1, location2);
        cuboid.getChunks().forEach(chunk -> {
            try {
                chunk.load();
                Chunk craftChunk = ((CraftChunk)chunk).getHandle();
                ChunkSection[] sections = ChunkUtil.copyChunkSections(this.cache.getArenaChunkAtLocation(chunk.getX(), chunk.getZ()));
                ChunkUtil.setChunkSections(craftChunk, sections);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Returns the type of the arena.
     * @return the type of the arena, which is always {@link ArenaType#DUPLICATE}
     */
    public ArenaType getType() {
        return ArenaType.DUPLICATE;
    }

    /**
     * Determines if the arena has been fully setup with two points and two spawns.
     * @return true if the arena is fully setup, false otherwise
     */
    public boolean isSetup() {
        return getLowerCorner() != null && getUpperCorner() != null && spawnA != null && spawnB != null;
    }

    /**
    * Calculates the maximum buildable height in the arena.
    * <p>
    * This method returns the Y-coordinate of the higher of the two spawn points
    * incremented by 8, which represents the additional height allowed for building.
    * 
    * @return the maximum build height in the arena
    */
    public int getMaxBuildHeight() {
        int highest = (int) (Math.max(spawnA.getY(), spawnB.getY()));
        return highest + 8;
    }

    public int getDeathZone() {
        int lowest = (int) (Math.min(spawnA.getY(), spawnB.getY()));
        return lowest - 8;
    }

    /**
     * Sets the active state of the arena.
     * <p>
     * In shared arenas, the active state is determined by the parent arena,
     * and setting the active state will have no effect.
     * 
     * @param active true if the arena should be active, false otherwise
     */
    public void setActive(boolean active) {
        if (getType() != ArenaType.SHARED) {
            this.active = active;
        }
    }

    public void save() {}

    /**
     * Deletes the arena from the cache and removes it from the list of loaded
     * arenas.
     */
    public void delete() {
        arenas.remove(this);
    }

}
