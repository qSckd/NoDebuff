package club.nodebuff.moon.util.hologram;

import club.nodebuff.moon.Moon;
import club.nodebuff.moon.util.hologram.placeholder.Placeholder;
import club.nodebuff.moon.util.CC;
import club.nodebuff.moon.util.fake.impl.hologram.FakeEntityArmorStand;
import club.nodebuff.moon.util.fake.impl.hologram.FakeEntityHorse;
import club.nodebuff.moon.util.fake.impl.hologram.FakeEntitySkull;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class HologramLine {
    private static final double SKULL_OFFSET = 55.87;
    private final FakeEntitySkull fakeEntitySkull;
    private final FakeEntityHorse fakeEntityHorse;
    private final PacketPlayOutAttachEntity attachEntityPacket;
    private final FakeEntityArmorStand fakeEntityArmorStand;
    private String line;

    public HologramLine(String line, Location location, int offset) {
        this.fakeEntitySkull = new FakeEntitySkull(-1, location.clone().add(0.0, 55.87 + (double)offset, 0.0));
        this.fakeEntityHorse = new FakeEntityHorse(-1, location);
        this.fakeEntityArmorStand = new FakeEntityArmorStand(-1, location.clone().add(0.0, offset, 0.0));
        this.fakeEntityHorse.setSize(-1700000);
        this.fakeEntityArmorStand.setSmall(true);
        this.fakeEntityArmorStand.setInvisible(true);
        this.fakeEntityArmorStand.setGravity(false);
        this.attachEntityPacket = new PacketPlayOutAttachEntity();
        this.updateLocation(location, offset);
        this.updateLine(line);
    }

    public void updateLocation(Location location, double offset) {
        this.fakeEntityArmorStand.teleport(location.clone().add(0.0, offset, 0.0));
        this.fakeEntitySkull.teleport(location.clone().add(0.0, 55.87 + offset, 0.0));
        this.fakeEntityHorse.teleport(location);
    }

    public void setup(Player player) {
        int version = 47;
        this.fakeEntityArmorStand.getCurrentlyViewing().remove(player.getUniqueId());
        this.fakeEntityHorse.getCurrentlyViewing().remove(player.getUniqueId());
        this.fakeEntitySkull.getCurrentlyViewing().remove(player.getUniqueId());
        if (version >= 47) {
            this.fakeEntityArmorStand.show(player);
            this.update(player);
        } else {
            this.fakeEntitySkull.show(player);
            this.fakeEntityHorse.show(player);
            this.update(player);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(this.attachEntityPacket);
        }
    }

    public void update(Player player) {
        PacketPlayOutEntityMetadata metadata;
        String formattedLine = this.line;
        for (Placeholder placeholder : Moon.get().getHologramHandler().getPlaceholders()) {
            formattedLine = placeholder.formatLine(player, formattedLine);
        }
        if (this.fakeEntityArmorStand.getCurrentlyViewing().contains(player.getUniqueId())) {
            this.fakeEntityArmorStand.setName(formattedLine);
            metadata = this.fakeEntityArmorStand.updateMetadata();
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(metadata);
        } else if (this.fakeEntityHorse.getCurrentlyViewing().contains(player.getUniqueId())) {
            this.fakeEntityHorse.setName(formattedLine);
            metadata = this.fakeEntityHorse.updateMetadata();
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(metadata);
        }
    }

    public void hide(Player player) {
        this.fakeEntitySkull.hide(player);
        this.fakeEntityHorse.hide(player);
        this.fakeEntityArmorStand.hide(player);
    }

    public void updateLine(String line) {
        Player player;
        line = CC.translate(line);
        if (this.line != null && this.line.equals(line)) {
            return;
        }
        this.line = line;
        for (UUID uuid : this.fakeEntityArmorStand.getCurrentlyViewing()) {
            player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            this.update(player);
        }
        for (UUID uuid : this.fakeEntityHorse.getCurrentlyViewing()) {
            player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            this.update(player);
        }
    }

    public void setupBulk(Set<UUID> uuids) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!uuids.contains(player.getUniqueId())) continue;
            this.setup(player);
        }
    }

    public void updateBulk(Set<UUID> uuids) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!uuids.contains(player.getUniqueId())) continue;
            this.update(player);
        }
    }

    public void hideBulk(Set<UUID> uuids) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!uuids.contains(player.getUniqueId())) continue;
            this.hide(player);
        }
    }

    public String getLine() {
        return this.line;
    }
}

