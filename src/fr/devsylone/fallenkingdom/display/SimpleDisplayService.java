package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class SimpleDisplayService implements DisplayService {

    private final DisplayType type;
    private final String value;
    private final Set<PlaceHolder> placeHolders;

    public SimpleDisplayService(@NotNull DisplayType type, @NotNull String value) {
        if (type == DisplayType.SCOREBOARD) {
            throw new IllegalArgumentException();
        }
        this.type = type;
        this.value = requireNonNull(value, "display value");
        if (!value.contains(PLACEHOLDER_START) || !value.contains(PLACEHOLDER_END)) {
            this.placeHolders = Collections.emptySet();
            return;
        }

        this.placeHolders = EnumSet.noneOf(PlaceHolder.class);
        for (PlaceHolder placeholder : PlaceHolder.values()) {
            if (value.contains(placeholder.getKey())) {
                this.placeHolders.add(placeholder);
            }
        }
    }

    @Override
    public boolean contains(@NotNull PlaceHolder placeHolder) {
        return this.placeHolders.contains(placeHolder);
    }

    @Override
    public boolean containsAny(@NotNull PlaceHolder... placeHolders) {
        for (PlaceHolder placeHolder : placeHolders) {
            if (this.placeHolders.contains(placeHolder)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(@NotNull Player player, @NotNull FkPlayer fkPlayer, @NotNull PlaceHolder... placeHolders) {
        if (!this.containsAny(placeHolders)) {
            return;
        }

        String replaced = this.value;
        for (PlaceHolder placeHolder : this.placeHolders) {
            replaced = placeHolder.replace(replaced, player, 0);
        }
        player.sendActionBar(replaced);
    }

    public @NotNull String value() {
        return this.value;
    }

    @Contract("_ -> new")
    public @NotNull SimpleDisplayService withValue(@NotNull String next) {
        return new SimpleDisplayService(this.type, next);
    }

    public @NotNull DisplayType type() {
        return this.type;
    }
}
