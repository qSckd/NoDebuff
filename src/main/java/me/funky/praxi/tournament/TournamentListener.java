package me.funky.praxi.tournament;

import me.funky.praxi.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/* Author: Zatrex
   Project: Shadow
   Date: 2024/5/15
 */

public class TournamentListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (Tournament.getCurrentTournament() != null && Tournament.getCurrentTournament().isParticipating(event.getPlayer()) && Profile.getByUuid(event.getPlayer().getUniqueId()).getParty() != null) {
            Tournament.getCurrentTournament().leave(Profile.getByUuid(event.getPlayer().getUniqueId()).getParty());
        }
    }

}
