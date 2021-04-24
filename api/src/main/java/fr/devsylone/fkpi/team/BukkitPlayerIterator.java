package fr.devsylone.fkpi.team;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;

class BukkitPlayerIterator implements Iterator<Player> {

    private final Iterator<UUID> uuidIterator;
    private @Nullable Player next;

    BukkitPlayerIterator(@NotNull Iterable<@NotNull UUID> iterable) {
        this(iterable.iterator());
    }

    BukkitPlayerIterator(@NotNull Iterator<@NotNull UUID> iterator) {
        this.uuidIterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return this.next != null || findNext();
    }

    @Override
    public @NotNull Player next() {
        if (this.next == null && !findNext()) {
            throw new NoSuchElementException();
        }
        final Player player = this.next;
        this.next = null;
        return player;
    }

    private boolean findNext() {
        while (this.uuidIterator.hasNext()) {
            final Player player = getPlayer(this.uuidIterator.next());
            if (player != null) {
                this.next = player;
                return true;
            }
        }
        return false;
    }
}
