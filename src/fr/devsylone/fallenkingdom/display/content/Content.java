package fr.devsylone.fallenkingdom.display.content;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static fr.devsylone.fallenkingdom.display.content.BaseDependantContent.BASE;
import static fr.devsylone.fallenkingdom.display.content.BaseDependantContent.INSIDE;
import static fr.devsylone.fallenkingdom.display.content.BaseDependantContent.OUTSIDE;
import static fr.devsylone.fallenkingdom.display.content.InterruptibleContent.INNER;
import static fr.devsylone.fallenkingdom.display.content.InterruptibleContent.INTERRUPT;
import static fr.devsylone.fallenkingdom.display.content.InterruptibleContent.INTERRUPTIBLE;

/**
 * Un contenu qui peut être rendu et utilisé dans un {@link fr.devsylone.fallenkingdom.display.DisplayService}.
 * <p>
 * Un contenu peut être constant avec l'implémentation {@link ConstantContent} ou dépendant de certains critères, comme
 * la position du joueur, avec l'implémentation {@link BaseDependantContent}.
 */
public interface Content {

    default boolean contains(@NotNull PlaceHolder placeHolder) {
        return containsAny(placeHolder);
    }

    boolean containsAny(@NotNull PlaceHolder... placeHolders);

    @NotNull String format(@NotNull Player player, @NotNull FkPlayer fkPlayer, @NotNull PlaceHolder... placeHolders);

    void save(@NotNull ConfigurationSection parent, @NotNull String name);

    static @NotNull Content fromConfig(@Nullable Object config) {
        if (config instanceof ConfigurationSection) {
            final ConfigurationSection section = (ConfigurationSection) config;
            final String type = section.getString("type");
            if (BASE.equals(type)) {
                return new BaseDependantContent(
                        fromConfig(section.get(INSIDE)),
                        fromConfig(section.get(OUTSIDE))
                );
            } else if (INTERRUPTIBLE.equals(type)) {
                return new InterruptibleContent(
                        fromConfig(section.get(INTERRUPT)),
                        fromConfig(section.get(INNER))
                );
            }
        }
        return new ConstantContent(String.valueOf(config));
    }
}
