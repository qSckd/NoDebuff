package club.nodebuff.moon.profile.meta.option;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import club.nodebuff.moon.Moon;
import club.nodebuff.moon.profile.times.Times;
import club.nodebuff.moon.profile.themes.Themes;
import club.nodebuff.moon.profile.option.killeffect.SpecialEffects;
import club.nodebuff.moon.profile.option.killmessages.KillMessages;
import club.nodebuff.moon.profile.option.trail.Trail;

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
	@Getter @Setter private Themes theme = Themes.valueOf(Moon.get().getSettingsConfig().getString("GENERAL.DEFAULT-THEME"));
}
