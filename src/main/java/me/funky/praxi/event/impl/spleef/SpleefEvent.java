package me.funky.praxi.event.impl.spleef;

import me.funky.praxi.util.config.BasicConfigurationFile;
import me.funky.praxi.Praxi;
import me.funky.praxi.event.Event;
import me.funky.praxi.event.game.EventGame;
import me.funky.praxi.event.game.EventGameLogic;
import me.funky.praxi.util.ItemBuilder;
import me.funky.praxi.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* Author: HmodyXD
   Project: Shadow
   Date: 2024/5/13
 */

public class SpleefEvent implements Event {

    @Setter private Location lobbyLocation;
    @Getter private final List<String> allowedMaps;
    @Getter private final List<BlockState> changedBlocks;

    public SpleefEvent() {
        BasicConfigurationFile config = Praxi.get().getEventsConfig();

        lobbyLocation = LocationUtil.deserialize(config.getString("EVENTS.SPLEEF.LOBBY_LOCATION"));

        allowedMaps = new ArrayList<>();
        allowedMaps.addAll(config.getStringList("EVENTS.SPLEEF.ALLOWED_MAPS"));
        this.changedBlocks = new ArrayList<>();
    }

    @Override
    public String getDisplayName() {
        return "Spleef";
    }

    @Override
    public String getDisplayName(EventGame game) {
        return "Spleef &7(FFA)";
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
            "Compete by knocking all",
            "opponents off of the platform."
        );
    }

    @Override
    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.DIAMOND_SPADE).build();
    }

    @Override
    public boolean canHost(Player player) {
        return player.hasPermission("moon.event.host.spleef");
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
        return new SpleefGameLogic(game);
    }

    @Override
    public void save() {
        FileConfiguration config = Praxi.get().getEventsConfig().getConfiguration();
        config.set("EVENTS.SPLEEF.LOBBY_LOCATION", LocationUtil.serialize(lobbyLocation));
        config.set("EVENTS.SPLEEF.ALLOWED_MAPS", allowedMaps);

        try {
            config.save(Praxi.get().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}