package me.funky.praxi.arena.impl;

import lombok.Getter;
import lombok.Setter;
import me.funky.praxi.Praxi;
import me.funky.praxi.arena.Arena;
import me.funky.praxi.arena.ArenaType;
import me.funky.praxi.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StandaloneArena extends Arena {

	private List<Arena> duplicates = new ArrayList<>();

	public StandaloneArena(String name, Location location1, Location location2) {
		super(name, location1, location2);
	}

    public StandaloneArena(String name, Location location1, Location location2, Location spawnA, Location spawnB) {
		super(name, location1, location2);
	}

	@Override
	public ArenaType getType() {
		return ArenaType.STANDALONE;
	}

	@Override
	public void save() {
		System.out.println("STANDALONE ARENA SAVE");
		String path = "arenas." + getName();

		FileConfiguration configuration = Praxi.get().getArenasConfig().getConfiguration();
		configuration.set(path, null);
		configuration.set(path + ".type", getType().name());
		configuration.set(path + ".spawnA", LocationUtil.serialize(spawnA));
		configuration.set(path + ".spawnB", LocationUtil.serialize(spawnB));
		configuration.set(path + ".cuboid.location1", LocationUtil.serialize(getLowerCorner()));
		configuration.set(path + ".cuboid.location2", LocationUtil.serialize(getUpperCorner()));
		configuration.set(path + ".kits", getKits());

		if (!duplicates.isEmpty()) {
			System.out.println("Duplicates not empty");
			int i = 0;

			for (Arena duplicate : duplicates) {
				i++;

				configuration.set(path + ".duplicates." + i + ".cuboid.location1", LocationUtil.serialize(duplicate.getLowerCorner()));
				configuration.set(path + ".duplicates." + i + ".cuboid.location2", LocationUtil.serialize(duplicate.getUpperCorner()));
				configuration.set(path + ".duplicates." + i + ".spawnA", LocationUtil.serialize(duplicate.getSpawnA()));
				configuration.set(path + ".duplicates." + i + ".spawnB", LocationUtil.serialize(duplicate.getSpawnB()));
			}
		} else {
			System.out.println("Duplicates empty");
		}

		try {
			configuration.save(Praxi.get().getArenasConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public List<Arena> getDuplicatesForArena(Arena arena) {
        List<Arena> specificDuplicates = new ArrayList<>();
        for (Arena duplicate : duplicates) {
            if (duplicate.getName().equals(arena.getName())) {
                specificDuplicates.add(duplicate);
            }
        }
        return specificDuplicates;
    }

	@Override
	public void delete() {
		super.delete();

		FileConfiguration configuration = Praxi.get().getArenasConfig().getConfiguration();
		configuration.set("arenas." + getName(), null);

		try {
			configuration.save(Praxi.get().getArenasConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
