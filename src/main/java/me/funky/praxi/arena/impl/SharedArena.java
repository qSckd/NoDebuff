package club.nodebuff.moon.arena.impl;

import club.nodebuff.moon.arena.Arena;
import club.nodebuff.moon.arena.ArenaType;
import club.nodebuff.moon.util.LocationUtil;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import club.nodebuff.moon.Moon;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@Setter
public class SharedArena extends Arena {

	public SharedArena(String name, Location location1, Location location2) {
        super(name, location1, location2);
    }

	@Override
	public ArenaType getType() {
		return ArenaType.SHARED;
	}

	@Override
	public void save() {
		String path = "arenas." + getName();

		FileConfiguration configuration = Moon.get().getArenasConfig().getConfiguration();
		configuration.set(path, null);
		configuration.set(path + ".type", getType().name());
		if (spawnA != null) {
          configuration.set(path + ".spawnA", LocationUtil.serialize(spawnA));
        }

		if (spawnB != null) {
          configuration.set(path + ".spawnB", LocationUtil.serialize(spawnB));
        }
		configuration.set(path + ".cuboid.location1", LocationUtil.serialize(getLowerCorner()));
		configuration.set(path + ".cuboid.location2", LocationUtil.serialize(getUpperCorner()));
		configuration.set(path + ".kits", getKits());

		try {
			configuration.save(Moon.get().getArenasConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete() {
		super.delete();

		FileConfiguration configuration = Moon.get().getArenasConfig().getConfiguration();
		configuration.set("arenas." + getName(), null);

		try {
			configuration.save(Moon.get().getArenasConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
