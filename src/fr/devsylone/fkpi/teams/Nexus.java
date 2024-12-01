package fr.devsylone.fkpi.teams;

import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static fr.devsylone.fkpi.teams.ChestsRoom.CHESTS_ROOM;
import static fr.devsylone.fkpi.teams.CrystalCore.BAR_COLOR;
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

    @Contract(pure = true)
    boolean isInside(@NotNull Player player);

    @Contract(pure = true)
    @NotNull Base getBase();

    void save(@NotNull ConfigurationSection config);

    static @NotNull Nexus fromConfig(@NotNull Base base, @NotNull ConfigurationSection config) {
        final String nexusType = config.getString("type");
        if (CORE.equals(nexusType)) {
            final String entity = config.getString(ENTITY);
            final UUID coreId = entity == null ? new UUID(0, 0) : UUID.fromString(entity);
            return new CrystalCore(base, coreId, BarColor.valueOf(config.getString(BAR_COLOR, "WHITE")));
        } else if (CHESTS_ROOM.equals(nexusType)) {
            final ChestsRoom chestsRoom = new ChestsRoom(base);
            chestsRoom.load(config);
            return chestsRoom;
        } else {
            throw new IllegalArgumentException("Unknown nexus type: " + nexusType);
        }
    }
}
