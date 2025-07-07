package fr.devsylone.fallenkingdom.display.content;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import fr.devsylone.fallenkingdom.Fk;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;

import static fr.devsylone.fallenkingdom.display.DisplayService.PLACEHOLDER_END;
import static fr.devsylone.fallenkingdom.display.DisplayService.PLACEHOLDER_START;
import static java.util.Objects.requireNonNull;

/**
 * Un contenu qui ne change jamais.
 * <p>
 * Il contient une base de texte qui peut être formatée avec des placeholders.
 */
public class ConstantContent implements Content {

    private static final boolean PAPI_ENABLED = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

    private final String value;
    private final Set<PlaceHolder> placeHolders;
    private final boolean hasPapiPlaceholders;

    public ConstantContent(@NotNull String value) {
        this.value = requireNonNull(value, "display value");

        // Vérification des placeholders internes
        if (!value.contains(PLACEHOLDER_START) || !value.contains(PLACEHOLDER_END)) {
            this.placeHolders = Collections.emptySet();
        } else {
            this.placeHolders = EnumSet.noneOf(PlaceHolder.class);
            for (PlaceHolder placeholder : PlaceHolder.values()) {
                if (value.contains(placeholder.getKey())) {
                    this.placeHolders.add(placeholder);
                }
            }
        }

        // Vérification des placeholders PAPI (format %identifier_params%)
        this.hasPapiPlaceholders = PAPI_ENABLED && value.contains("%") && value.indexOf('%') != value.lastIndexOf('%');
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
    public @NotNull String format(@NotNull Player player, @NotNull FkPlayer fkPlayer, @NotNull PlaceHolder... placeHolders) {
        if (fkPlayer.useFormattedText()) {
            String replaced = this.value;

            // Traitement des placeholders internes
            for (PlaceHolder placeHolder : this.placeHolders) {
                replaced = placeHolder.replaceMultiple(replaced, player);
            }

            // Traitement des placeholders PAPI si disponible
            if (this.hasPapiPlaceholders) {
                try {
                    replaced = PlaceholderAPI.setPlaceholders(player, replaced);
                } catch (Exception e) {
                    // En cas d'erreur PAPI, on continue sans traiter les placeholders PAPI
                    Fk.getInstance().getLogger().log(Level.WARNING, "Erreur lors du traitement des placeholders PAPI", e);
                }
            }

            return replaced;
        } else {
            return ChatUtils.translateColorCodeToAmpersand(this.value);
        }
    }

    @Override
    public void save(@NotNull ConfigurationSection parent, @NotNull String name) {
        parent.set(name, this.value);
    }

    public @NotNull String content() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConstantContent)) return false;
        ConstantContent that = (ConstantContent) o;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return "\"" + this.value + "\"";
    }
}
