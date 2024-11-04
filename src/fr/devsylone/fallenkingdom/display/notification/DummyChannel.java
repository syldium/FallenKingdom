package fr.devsylone.fallenkingdom.display.notification;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * N'affiche pas la notification.
 * <p>
 * L'utilisateur peut vouloir masquer la notification tout de suite, mais l'afficher
 * plus tard avec un {@link fr.devsylone.fallenkingdom.scoreboard.PlaceHolder}.
 */
public class DummyChannel implements NotificationChannel {

    @Override
    public String toString() {
        return "DummyChannel";
    }

    @Override
    public void send(@NotNull Player player, @NotNull GameNotification notification) {

    }
}
