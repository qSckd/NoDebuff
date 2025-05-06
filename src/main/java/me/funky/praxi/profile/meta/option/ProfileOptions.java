package me.funky.praxi.profile.meta.option;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.funky.praxi.Praxi;
import me.funky.praxi.profile.times.Times;
import me.funky.praxi.profile.themes.Themes;
import me.funky.praxi.profile.option.killeffect.SpecialEffects;
import me.funky.praxi.profile.option.killmessages.KillMessages;
import me.funky.praxi.profile.option.trail.Trail;

@Accessors(fluent = true)
public class ProfileOptions {

	@Getter @Setter private boolean showScoreboard = true;
	@Getter @Setter private boolean receiveDuelRequests = true;
	@Getter @Setter private boolean allowSpectators = true;
    @Getter @Setter private boolean spectatorMessages = true;
	@Getter @Setter private boolean showPlayers = true;
	@Getter @Setter private Times time = Times.DAY;
	@Getter @Setter private SpecialEffects killEffect = SpecialEffects.NONE;
	@Getter @Setter private KillMessages killMessage = KillMessages.NONE;
    @Getter @Setter private Trail trail = Trail.NONE;
	@Getter @Setter private Themes theme = Themes.valueOf(Praxi.get().getSettingsConfig().getString("GENERAL.DEFAULT-THEME"));
}
