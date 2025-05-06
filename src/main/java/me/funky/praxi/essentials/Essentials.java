package me.funky.praxi.essentials;

import me.funky.praxi.Praxi;
import me.funky.praxi.essentials.event.SpawnTeleportEvent;
import me.funky.praxi.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Essentials {

	private Praxi praxi;
	private Location spawn;
    private Location holo;
    private Location mainholo;

	public Essentials(Praxi praxi) {
		this.praxi = praxi;
		this.spawn = LocationUtil.deserialize(praxi.getSettingsConfig().getStringOrDefault("GENERAL.SPAWN-LOCATION", null));
        this.mainholo = LocationUtil.deserialize(praxi.getHologramConfig().getStringOrDefault("MAIN.LOCATION", null));
        this.holo = LocationUtil.deserialize(praxi.getHologramConfig().getStringOrDefault("LEADERBOARDS.ELO.LOCATION", null));
	}

	public void setSpawn(Location location) {
		spawn = location;

		if (spawn == null) {
			praxi.getSettingsConfig().getConfiguration().set("GENERAL.SPAWN-LOCATION", null);
		} else {
			praxi.getSettingsConfig().getConfiguration().set("GENERAL.SPAWN-LOCATION", LocationUtil.serialize(this.spawn));
		}

		try {
			praxi.getSettingsConfig().getConfiguration().save(praxi.getSettingsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void setMainHolo(Location location) {
		mainholo = location;

		if (mainholo == null) {
			praxi.getHologramConfig().getConfiguration().set("MAIN.LOCATION", null);
		} else {
			praxi.getHologramConfig().getConfiguration().set("MAIN.LOCATION", LocationUtil.serialize(this.mainholo));
		}

		try {
			praxi.getHologramConfig().getConfiguration().save(praxi.getHologramConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void setHolo(Location location) {
		holo = location;

		if (holo == null) {
			praxi.getHologramConfig().getConfiguration().set("LEADERBOARDS.ELO.LOCATION", null);
		} else {
			praxi.getHologramConfig().getConfiguration().set("LEADERBOARDS.ELO.LOCATION", LocationUtil.serialize(this.holo));
		}

		try {
			praxi.getHologramConfig().getConfiguration().save(praxi.getHologramConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void teleportToSpawn(Player player) {
		Location location = spawn == null ? praxi.getServer().getWorlds().get(0).getSpawnLocation() : spawn;

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