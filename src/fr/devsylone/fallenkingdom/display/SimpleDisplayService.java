package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.display.content.Content;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleDisplayService implements DisplayService {

    private final DisplayType type;
    private final Content content;

    public SimpleDisplayService(@NotNull DisplayType type, @NotNull Content content) {
        if (type == DisplayType.SCOREBOARD) {
            throw new IllegalArgumentException();
        }
        this.type = type;
        this.content = content;
    }

    @Override
    public boolean contains(@NotNull PlaceHolder placeHolder) {
        return this.content.contains(placeHolder);
    }

    @Override
    public boolean containsAny(@NotNull PlaceHolder... placeHolders) {
        return this.content.containsAny(placeHolders);
    }

    @Override
    public void update(@NotNull Player player, @NotNull FkPlayer fkPlayer, @NotNull PlaceHolder... placeHolders) {
        if (placeHolders.length != 0 && !this.containsAny(placeHolders)) {
            return;
        }
        this.show(player, this.content.format(player, fkPlayer, placeHolders));
    }

    public abstract void show(@NotNull Player player, @NotNull String message);

    public @NotNull Content content() {
        return this.content;
    }

    @Contract("_ -> new")
    public abstract @NotNull SimpleDisplayService withValue(@NotNull Content next);

    public @NotNull DisplayType type() {
        return this.type;
    }

    abstract void save(@NotNull ConfigurationSection section);
}
