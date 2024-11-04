package fr.devsylone.fallenkingdom.display.notification;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Affiche tout de suite une notification dans le chat du joueur.
 */
public class ChatChannel implements NotificationChannel {

    @Override
    public void send(@NotNull Player player, @NotNull GameNotification notification) {
        player.sendMessage(notification.message(player));
    }

    @Override
    public String toString() {
        return "ChatChannel";
    }
}
