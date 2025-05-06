package club.nodebuff.moon.adapter.lunar;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.richpresence.RichPresenceModule;
import com.lunarclient.apollo.module.richpresence.ServerRichPresence;
import com.lunarclient.apollo.player.ApolloPlayer;
import club.nodebuff.moon.match.Match;
import club.nodebuff.moon.profile.Profile;
import org.bukkit.entity.Player;

import java.util.Optional;

public class RichPresence {

    private final RichPresenceModule richPresenceModule = Apollo.getModuleManager().getModule(RichPresenceModule.class);

    public void overrideServerRichPresence(Player viewer) {
        Profile profile = Profile.getByUuid(viewer.getUniqueId());
        Match match = profile.getMatch();
        Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());

        apolloPlayerOpt.ifPresent(apolloPlayer -> {
            this.richPresenceModule.overrideServerRichPresence(apolloPlayer, ServerRichPresence.builder()
                .gameName("Practice")
                .gameVariantName(match.getKit().getName())
                .gameState(match.getState().toString())
                .playerState(profile.getState().toString())
                .mapName(match.getArena().getName())
                .subServerName("Practice-1")
                .build()
            );
        });
    }

    public void resetServerRichPresence(Player viewer) {
        Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());
        apolloPlayerOpt.ifPresent(this.richPresenceModule::resetServerRichPresence);
    }

}