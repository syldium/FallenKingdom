package fr.devsylone.fallenkingdom.display.content;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Un contenu sur lequel peut se superposer un autre contenu.
 * <p>
 * Le contenu interrompu contient généralement un placeholder qui contient occasionnellement du texte,
 * comme {@code REGION_CHANGE}.
 */
public class InterruptibleContent implements Content {

    private final Content interrupt;
    private final Content inner;

    public InterruptibleContent(@NotNull Content interrupt, @NotNull Content inner) {
        this.interrupt = interrupt;
        this.inner = inner;
    }

    @Override
    public boolean containsAny(@NotNull PlaceHolder... placeHolders) {
        return this.interrupt.containsAny(placeHolders) || this.inner.containsAny(placeHolders);
    }

    /**
     * Vérifie si ce contenu contient des placeholders PAPI
     * @return true si des placeholders PAPI sont présents dans interrupt ou inner
     */
    public boolean containsPapiPlaceholders() {
        return (this.interrupt instanceof ConstantContent && ((ConstantContent) this.interrupt).containsPapiPlaceholders()) ||
                (this.inner instanceof ConstantContent && ((ConstantContent) this.inner).containsPapiPlaceholders()) ||
                (this.interrupt instanceof BaseDependantContent && ((BaseDependantContent) this.interrupt).containsPapiPlaceholders()) ||
                (this.inner instanceof BaseDependantContent && ((BaseDependantContent) this.inner).containsPapiPlaceholders()) ||
                (this.interrupt instanceof InterruptibleContent && ((InterruptibleContent) this.interrupt).containsPapiPlaceholders()) ||
                (this.inner instanceof InterruptibleContent && ((InterruptibleContent) this.inner).containsPapiPlaceholders());
    }

    @Override
    public @NotNull String format(@NotNull Player player, @NotNull FkPlayer fkPlayer, @NotNull PlaceHolder... placeHolders) {
        String result = this.interrupt.format(player, fkPlayer, placeHolders);
        if (ChatColor.stripColor(result).isEmpty()) {
            result = this.inner.format(player, fkPlayer, placeHolders);
        }
        return result;
    }

    static final String INTERRUPTIBLE = "interruptible";
    static final String INTERRUPT = "interrupt";
    static final String INNER = "inner";

    @Override
    public void save(@NotNull ConfigurationSection parent, @NotNull String name) {
        final ConfigurationSection section = parent.createSection(name);
        section.set("type", INTERRUPTIBLE);
        this.interrupt.save(section, INTERRUPT);
        this.inner.save(section, INNER);
    }
}