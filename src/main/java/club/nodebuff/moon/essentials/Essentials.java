package club.nodebuff.moon.essentials;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.essentials.event.SpawnTeleportEvent;
import club.nodebuff.moon.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Essentials {

	private Moon Moon;
	private Location spawn;
    private Location holo;
    private Location mainholo;

	public Essentials(Moon Moon) {
		this.Moon = Moon;
		this.spawn = LocationUtil.deserialize(Moon.getSettingsConfig().getStringOrDefault("GENERAL.SPAWN-LOCATION", null));
        this.mainholo = LocationUtil.deserialize(Moon.getHologramConfig().getStringOrDefault("MAIN.LOCATION", null));
        this.holo = LocationUtil.deserialize(Moon.getHologramConfig().getStringOrDefault("LEADERBOARDS.ELO.LOCATION", null));
	}

	public void setSpawn(Location location) {
		spawn = location;

		if (spawn == null) {
			Moon.getSettingsConfig().getConfiguration().set("GENERAL.SPAWN-LOCATION", null);
		} else {
			Moon.getSettingsConfig().getConfiguration().set("GENERAL.SPAWN-LOCATION", LocationUtil.serialize(this.spawn));
		}

		try {
			Moon.getSettingsConfig().getConfiguration().save(Moon.getSettingsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void setMainHolo(Location location) {
		mainholo = location;

		if (mainholo == null) {
			Moon.getHologramConfig().getConfiguration().set("MAIN.LOCATION", null);
		} else {
			Moon.getHologramConfig().getConfiguration().set("MAIN.LOCATION", LocationUtil.serialize(this.mainholo));
		}

		try {
			Moon.getHologramConfig().getConfiguration().save(Moon.getHologramConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void setHolo(Location location) {
		holo = location;

		if (holo == null) {
			Moon.getHologramConfig().getConfiguration().set("LEADERBOARDS.ELO.LOCATION", null);
		} else {
			Moon.getHologramConfig().getConfiguration().set("LEADERBOARDS.ELO.LOCATION", LocationUtil.serialize(this.holo));
		}

		try {
			Moon.getHologramConfig().getConfiguration().save(Moon.getHologramConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void teleportToSpawn(Player player) {
		Location location = spawn == null ? Moon.getServer().getWorlds().get(0).getSpawnLocation() : spawn;

		SpawnTeleportEvent event = new SpawnTeleportEvent(player, location);
		event.call();

		if (!event.isCancelled() && event.getLocation() != null) {
			player.teleport(event.getLocation());
		}
	}

	public int clearEntities(World world) {
		int removed = 0;

		for (Entity entity : world.getEntities()) {
			if (entity.getType() == EntityType.PLAYER) {
				continue;
			}

			removed++;
			entity.remove();
		}

		return removed;
	}

	public int clearEntities(World world, EntityType... excluded) {
		int removed = 0;

		entityLoop:
		for (Entity entity : world.getEntities()) {
			if (entity instanceof Item) {
				removed++;
				entity.remove();
				continue entityLoop;
			}

			for (EntityType type : excluded) {
				if (entity.getType() == EntityType.PLAYER) {
					continue entityLoop;
				}

				if (entity.getType() == type) {
					continue entityLoop;
				}
			}

			removed++;
			entity.remove();
		}

		return removed;
	}

}