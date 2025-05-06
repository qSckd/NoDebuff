package club.nodebuff.moon.util.hologram;

import java.beans.ConstructorProperties;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class UpdatingHologram extends Hologram {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public UpdatingHologram(Location location, int delayMillis) {
        super(location);
        this.executorService.scheduleAtFixedRate(new UpdatingHologramTask(this), delayMillis, delayMillis, TimeUnit.MILLISECONDS);
    }

    public void stopTask() {
        this.executorService.shutdown();
    }

    public static class UpdatingHologramTask
    implements Runnable {
        private final UpdatingHologram hologram;

        @Override
        public void run() {
            for (UUID uuid : this.hologram.getSetup()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                this.hologram.update(player);
            }
        }

        @ConstructorProperties(value={"hologram"})
        public UpdatingHologramTask(UpdatingHologram hologram) {
            this.hologram = hologram;
        }
    }
}