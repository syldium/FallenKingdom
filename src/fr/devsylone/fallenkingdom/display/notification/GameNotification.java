package fr.devsylone.fallenkingdom.display.notification;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Décrit un message daté.
 */
public interface GameNotification {

    long timestamp();

    @NotNull String message(@NotNull Player player);
}
