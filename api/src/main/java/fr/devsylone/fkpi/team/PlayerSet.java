package fr.devsylone.fkpi.team;

import fr.devsylone.fkpi.util.ForwardingIterable;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public interface PlayerSet extends Audience, Iterable<UUID> {

    boolean hasPlayer(@NotNull String playerName);

    boolean hasPlayer(@NotNull UUID playerUniqueId);

    @NotNull @UnmodifiableView Set<UUID> playersUniqueIds();

    @NotNull @UnmodifiableView Set<String> playersNames();

    int size();

    boolean isEmpty();

    default @NotNull Iterable<org.bukkit.entity.Player> bukkitPlayers() {
        return new ForwardingIterable<>(this, BukkitPlayerIterator::new);
    }

    default @NotNull Iterator<org.bukkit.entity.Player> bukkitPlayersIterator() {
        return new BukkitPlayerIterator(this);
    }
}
