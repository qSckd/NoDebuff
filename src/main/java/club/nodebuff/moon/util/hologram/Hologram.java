package club.nodebuff.moon.util.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Hologram {
    private final List<HologramLine> lines = new ArrayList<HologramLine>();
    private final Set<UUID> setup = new HashSet<UUID>();
    private Location location;

    public Hologram(Location location) {
        this.updateLocation(location);
    }

    public void setup(Player player) {
        this.setup.add(player.getUniqueId());
        for (HologramLine line : this.lines) {
            line.setup(player);
        }
    }

    public void update(Player player) {
        if (!this.setup.contains(player.getUniqueId())) {
            return;
        }
        for (HologramLine line : this.lines) {
            line.update(player);
        }
    }

    public void hide(Player player) {
        if (!this.setup.contains(player.getUniqueId())) {
            return;
        }
        this.setup.remove(player.getUniqueId());
        for (HologramLine line : this.lines) {
            line.hide(player);
        }
    }

    public boolean isSetup(UUID uuid) {
        return this.setup.contains(uuid);
    }

    public void addLine(String line) {
        this.addLine(this.lines.size(), line);
    }

    public void addLine(int index, String line) {
        HologramLine hologramLine = new HologramLine(line, this.location, 0);
        this.lines.add(index, hologramLine);
        this.updateRelativeLocation();
        hologramLine.setupBulk(this.setup);
    }

    public HologramLine getLine(int index) {
        if (index >= this.lines.size()) {
            return null;
        }
        return this.lines.get(index);
    }

    public List<HologramLine> getLines() {
        return this.lines;
    }

    public boolean isEmpty() {
        return this.getLines().isEmpty();
    }

    public void removeLine(int index) {
        if (index >= this.lines.size()) {
            return;
        }
        HologramLine line = this.lines.remove(index);
        if (line == null) {
            return;
        }
        line.hideBulk(this.setup);
        this.updateRelativeLocation();
    }

    public void updateRelativeLocation() {
        int size = this.lines.size();
        if (size <= 0) {
            return;
        }
        for (int i = 0; i < size; ++i) {
            HologramLine hologramLine = this.lines.get(i);
            hologramLine.updateLocation(this.location, (double)(size - i) * 0.3);
        }
    }

    public void updateLocation(Location location) {
        this.location = location;
        this.updateRelativeLocation();
    }

    public Set<UUID> getSetup() {
        return this.setup;
    }

    public Location getLocation() {
        return this.location;
    }
}

