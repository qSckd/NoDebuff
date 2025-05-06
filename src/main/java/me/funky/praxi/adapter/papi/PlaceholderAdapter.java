package me.funky.praxi.adapter.papi;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.funky.praxi.Praxi;
import me.funky.praxi.profile.Profile;
import me.funky.praxi.profile.meta.option.ProfileOptions;
import me.funky.praxi.util.TimeUtil;
import me.funky.praxi.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/* Author: Zatrex
   Project: Shadow
   Date: 2024/5/13
   TODO: Add placeholder for divisions bar
 */

@RequiredArgsConstructor
public class PlaceholderAdapter extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "moon";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Zatrex";
    }

    @Override
    public @NotNull String getVersion() {
        return Praxi.get().getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().isPluginEnabled(Praxi.get());
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) return "Null Player";
        if (!player.isOnline()) return "Offline Player";
        Profile profile = Profile.getByUuid(player.getUniqueId());
        ProfileOptions options = profile.getOptions();

        if (identifier.equalsIgnoreCase("player_theme")) {
            return String.valueOf("&" + Profile.getByUuid(player.getUniqueId()).getOptions().theme().getColor().getChar());

        } else if (identifier.equalsIgnoreCase("player_killeffect")) {
            return options.killEffect().toString();

        } else if (identifier.equalsIgnoreCase("player_elo")) {
            return String.valueOf(profile.getGlobalElo());

        } else if (identifier.equalsIgnoreCase("player_wins")) {
            return String.valueOf(profile.getWins());

        } else if (identifier.equalsIgnoreCase("player_loses")) {
            return String.valueOf(profile.getLoses());

        } else if (identifier.equalsIgnoreCase("player_coins")) {
            return String.valueOf(profile.getCoins());

        } else if (identifier.equalsIgnoreCase("player_exp")) {
            return String.valueOf(profile.getExperience());

        } else if (identifier.equalsIgnoreCase("player_division")) {
            return CC.translate(profile.getDivision().getDisplayName());

        } else if (identifier.equalsIgnoreCase("player_division_bar")) {
            return "TODO";
        }
        return "PapiBug";
    }
}