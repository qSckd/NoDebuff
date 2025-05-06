package me.funky.praxi.profile.option.trail.listener;

import lombok.NonNull;
import me.funky.praxi.Praxi;
import me.funky.praxi.match.MatchState;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.option.trail.Trail;
import me.funky.praxi.util.PlayerUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class TrailListener implements Listener {
    private final Map<Projectile, BukkitTask> trailTasks = new HashMap<>();

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player) {
            Player shooter = (Player) projectile.getShooter();
            Profile shooterProfile = Profile.getByUuid(shooter.getUniqueId());

            Trail selectedTrail = shooterProfile.getOptions().trail();
            if (shooterProfile.isInFight() && selectedTrail != null && selectedTrail != Trail.NONE) {
                createTrailEffect(shooter, projectile, selectedTrail);
            }
        }
    }

    private void createTrailEffect(Player player, Projectile projectile, @NonNull Trail selectedTrail) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (projectile.isDead() || !projectile.isValid()) {
                    cancelTrailTask(projectile);
                    return;
                }

                Location location = projectile.getLocation();
                Effect trail = selectedTrail.getTrail();

                if (trail != null) {
                    PlayerUtil.playSimpleParticleEffect(location, trail);
                }
            }
        }.runTaskTimer(Praxi.get(), 0L, 1L);

        trailTasks.put(projectile, task);
    }

    private void cancelTrailTask(Projectile projectile) {
        if (trailTasks.containsKey(projectile)) {
            BukkitTask task = trailTasks.get(projectile);
            task.cancel();
            trailTasks.remove(projectile);
        }
    }
}
