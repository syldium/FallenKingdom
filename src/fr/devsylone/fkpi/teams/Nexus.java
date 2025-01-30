package fr.devsylone.fkpi.teams;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static fr.devsylone.fkpi.teams.ChestsRoom.CHESTS_ROOM;
import static fr.devsylone.fkpi.teams.CrystalCore.CORE;
import static fr.devsylone.fkpi.teams.CrystalCore.ENTITY;

/**
 * Un centre de base qu'une équipe peut venir attaquer d'une manière ou d'une autre.
 * <p>
 * Voir {@link ChestsRoom} pour une région modifiable par les joueurs en plaçant des coffres.
 * Voir {@link Nexus} pour une région fixe avec un crystal à frapper.
 */
public interface Nexus {

    /**
     * Teste si une location est à l'intérieur du nexus actuel.
     *
     * @param test la location à tester
     * @return {@code true} si la location est à l'intérieur, {@code false} sinon
     */
    @Contract(pure = true)
    boolean contains(@NotNull Location test);

    /**
     * Indique qu'un joueur est entré dans le nexus.
     * <p>
     * Généralement cela a lieu lorsque le joueur a bougé et que {@link #contains(Location)} est vrai,
     * mais cela peut aussi être appelé manuellement.
     *
     * @param player Un défenseur ou attaquant entrant dans le nexus
     */
    void addEnemyInside(@NotNull Player player);

    /**
     * Indique qu'un joueur est sorti du nexus.
     *
     * @param player Un défenseur ou attaquant sortant du nexus
     */
    void removeEnemyInside(@NotNull Player player);

    /**
     * Teste si un joueur a été ajouté à la liste des joueurs à l'intérieur du nexus.
     *
     * @param player Le joueur à tester
     * @return {@code true} si le joueur est à l'intérieur, {@code false} sinon
     * @see #contains(Location) pour vérifier si la position effective du joueur est à l'intérieur
     */
    @Contract(pure = true)
    boolean isInside(@NotNull Player player);

    /**
     * Détermine si le nexus est dans une zone délimitable.
     * <p>
     * Usuellement, le nexus est défini sauf s'il n'est pas localisable dans le monde.
     *
     * @return {@code true} si le nexus est défini, {@code false} sinon
     */
    @Contract(pure = true)
    default boolean isDefined() {
        return true;
    }

    @Contract(pure = true)
    @NotNull Base getBase();

    /**
     * Réinitialise le nexus à son état initial.
     * <p>
     * Le nexus doit être utilisable ensuite.
     */
    void reset();

    /**
     * Supprime les éléments que le nexus a ajoutés au monde.
     * <p>
     * Le nexus peut ne plus être utilisable après cela.
     */
    void remove();

    void save(@NotNull ConfigurationSection config);

    static @NotNull Nexus fromConfig(@NotNull Base base, @NotNull ConfigurationSection config) {
        final String nexusType = config.getString("type");
        if (CORE.equals(nexusType)) {
            final String entity = config.getString(ENTITY);
            final UUID coreId = entity == null ? new UUID(0, 0) : UUID.fromString(entity);
            final int damage = config.getInt(CrystalCore.DAMAGE);
            return new CrystalCore(base, coreId, base.getTeam().getColor().getBukkitChatColor(), damage);
        } else if (CHESTS_ROOM.equals(nexusType)) {
            final ChestsRoom chestsRoom = new ChestsRoom(base);
            chestsRoom.load(config);
            return chestsRoom;
        } else {
            throw new IllegalArgumentException("Unknown nexus type: " + nexusType);
        }
    }
}
