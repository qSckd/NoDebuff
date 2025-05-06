package club.nodebuff.moon.adapter.lunar;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.notification.Notification;
import com.lunarclient.apollo.module.notification.NotificationModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

public class EventNotification {

    private final NotificationModule notificationModule = Apollo.getModuleManager().getModule(NotificationModule.class);

    private final Notification eventAnnouncement = Notification.builder()
        .titleComponent(Component.text("New Event is Starting..", NamedTextColor.GREEN))
        .descriptionComponent(Component.text("The Event is Starting in 1min.", NamedTextColor.WHITE)
            .appendNewline()
            .append(Component.text("Make sure to join!", NamedTextColor.WHITE))
            .appendNewline()
            .append(Component.text("Thanks.", NamedTextColor.AQUA))
        )
        .resourceLocation("icons/golden_apple.png") // This field is optional
        .displayTime(Duration.ofSeconds(5))
        .build();

    public void displayNotification(Player viewer) {
        Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());
        apolloPlayerOpt.ifPresent(apolloPlayer -> this.notificationModule.displayNotification(apolloPlayer, this.eventAnnouncement));
    }

    public void resetNotifications(Player viewer) {
        Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());
        apolloPlayerOpt.ifPresent(this.notificationModule::resetNotifications);
    }

}