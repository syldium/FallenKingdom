package fr.devsylone.fallenkingdom.display.notification;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Présente une notification à un joueur.
 */
public interface NotificationChannel {

    void send(@NotNull Player player, @NotNull GameNotification notification);

    static @NotNull String name(@NotNull NotificationChannel channel) {
        if (channel instanceof ChatChannel) {
            return "chat";
        }
        return "dummy";
    }

    static @NotNull NotificationChannel fromConfig(@Nullable String config) {
        if (config == null || config.equals("chat")) {
            return new ChatChannel();
        }
        return new DummyChannel();
    }
}
