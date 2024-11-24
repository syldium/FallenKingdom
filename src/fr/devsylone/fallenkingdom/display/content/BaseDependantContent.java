package fr.devsylone.fallenkingdom.display.content;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fkpi.FkPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Présente un texte différent en fonction de la position du joueur par rapport à une base.
 */
public class BaseDependantContent implements Content {

    private final Content inside;
    private final Content outside;

    public BaseDependantContent(@NotNull Content inside, @NotNull Content outside) {
        this.inside = inside;
        this.outside = outside;
    }

    @Override
    public boolean contains(@NotNull PlaceHolder placeHolder) {
        return placeHolder == PlaceHolder.REGION || this.inside.contains(placeHolder) || this.outside.contains(placeHolder);
    }

    @Override
    public boolean containsAny(@NotNull PlaceHolder... placeHolders) {
        for (PlaceHolder placeHolder : placeHolders) {
            if (placeHolder == PlaceHolder.REGION) {
                return true;
            }
        }
        return this.inside.containsAny(placeHolders) || this.outside.containsAny(placeHolders);
    }

    @Override
    public @NotNull String format(@NotNull Player player, @NotNull FkPlayer fkPlayer, @NotNull PlaceHolder... placeHolders) {
        if (FkPI.getInstance().getTeamManager().getBase(player.getLocation()).isPresent()) {
            return this.inside.format(player, fkPlayer, placeHolders);
        } else {
            return this.outside.format(player, fkPlayer, placeHolders);
        }
    }

    static final String BASE = "base";
    static final String INSIDE = "inside";
    static final String OUTSIDE = "outside";

    @Override
    public void save(@NotNull ConfigurationSection parent, @NotNull String name) {
        final ConfigurationSection section = parent.createSection(name);
        section.set("type", BASE);
        this.inside.save(section, INSIDE);
        this.outside.save(section, OUTSIDE);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BaseDependantContent)) return false;
        BaseDependantContent that = (BaseDependantContent) o;
        return this.inside.equals(that.inside) && this.outside.equals(that.outside);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.inside, this.outside);
    }

    @Override
    public String toString() {
        return "BaseDependantContent{" +
                "inside=" + this.inside +
                ", outside=" + this.outside +
                '}';
    }
}
