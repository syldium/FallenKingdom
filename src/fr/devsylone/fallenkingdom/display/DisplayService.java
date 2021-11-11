package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Affiche du texte avec des placeholders sur n'importe quel média.
 */
public interface DisplayService {

    String PLACEHOLDER_START = "{";
    String PLACEHOLDER_END = "}";

    default boolean contains(@NotNull PlaceHolder placeHolder) {
        return containsAny(placeHolder);
    }

    boolean containsAny(@NotNull PlaceHolder... placeHolders);

    void update(@NotNull Player player, @NotNull FkPlayer fkPlayer, @NotNull PlaceHolder... placeHolders);

    void hide(@NotNull Player player, @NotNull FkPlayer fkPlayer);
}
