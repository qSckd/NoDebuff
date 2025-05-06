package club.nodebuff.moon.event.impl.sumo;

import lombok.Getter;
import lombok.Setter;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.event.Event;
import club.nodebuff.moon.event.game.EventGame;
import club.nodebuff.moon.event.game.EventGameLogic;
import club.nodebuff.moon.util.ItemBuilder;
import club.nodebuff.moon.util.LocationUtil;
import club.nodebuff.moon.util.config.BasicConfigurationFile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SumoEvent implements Event {

    @Getter
    private final List<String> allowedMaps;
    @Setter
    private Location lobbyLocation;

    public SumoEvent() {
        BasicConfigurationFile config = Moon.get().getEventsConfig();

        lobbyLocation = LocationUtil.deserialize(config.getString("EVENTS.SUMO.LOBBY_LOCATION"));

        allowedMaps = new ArrayList<>();
        allowedMaps.addAll(config.getStringList("EVENTS.SUMO.ALLOWED_MAPS"));
    }

    @Override
    public String getDisplayName() {
        return "Sumo";
    }

    @Override
    public String getDisplayName(EventGame game) {
        return "Sumo &7(Solos)";
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "Compete by knocking your",
                "opponent off of the platform."
        );
    }

    @Override
    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.LEASH).build();
    }

    @Override
    public boolean canHost(Player player) {
        return player.hasPermission("moon.event.host.sumo");
    }

    @Override
    public List<Listener> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public List<Object> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public EventGameLogic start(EventGame game) {
        return new SumoGameLogic(game);
    }

    @Override
    public void save() {
        FileConfiguration config = Moon.get().getEventsConfig().getConfiguration();
        config.set("EVENTS.SUMO.LOBBY_LOCATION", LocationUtil.serialize(lobbyLocation));
        config.set("EVENTS.SUMO.ALLOWED_MAPS", allowedMaps);

        try {
            config.save(Moon.get().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}