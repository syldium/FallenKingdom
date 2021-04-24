package fr.devsylone.fkpi.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.Buildable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static fr.devsylone.fkpi.FkPI.teams;

/**
 * A team of Fallen Kingdom players.
 */
@NonExtendable
public interface FkTeam extends PlayerSet, ComponentLike, Buildable<FkTeam, FkTeam.Builder> {

    /**
     * Gets the name of this team.
     *
     * @return The name of this team
     */
    @NotNull String name();

    /**
     * Gets the color of this team.
     *
     * @return The team color
     */
    @NotNull TextColor color();

    /**
     * Gets the rgb value of the color, packed into an int.
     *
     * @return The rgb value
     */
    default int colorCode() {
        return this.color().value();
    }

    /**
     * Returns the team's base.
     *
     * @return The base, if any
     */
    @NotNull Optional<@NotNull Base> base();

    /**
     * Adds the specified player to this team.
     *
     * @param playerName The name of the player to add
     * @return The result of the team change
     */
    @NotNull TeamChangeResult addPlayer(@NotNull String playerName);

    /**
     * Adds the specified player to this team.
     *
     * @param playerUniqueId The unique identifier of the player
     * @return The result of the team change
     */
    @NotNull TeamChangeResult addPlayer(@NotNull UUID playerUniqueId);

    /**
     * Removes the specified player from this team.
     *
     * @param playerName The name of the player to remove
     * @return Whether the player was in this team
     */
    boolean removePlayer(@NotNull String playerName);

    /**
     * Removes the specified player from this team.
     *
     * @param playerUniqueId The player's unique identifier
     * @return Whether the player was in this team
     */
    boolean removePlayer(@NotNull UUID playerUniqueId);

    /**
     * Creates a new builder to build a team.
     *
     * @return A new builder
     */
    @Contract("-> new")
    static @NotNull Builder builder() {
        return teams().teamBuilder();
    }

    /**
     * Creates a new builder to build a team and sets the team name.
     *
     * @param name The name of the team
     * @return A new builder
     */
    @Contract("_ -> new")
    static @NotNull Builder builder(@NotNull String name) {
        return teams().teamBuilder(name);
    }

    /**
     * Represents a builder to create {@link FkTeam} instances.
     */
    interface Builder extends Buildable.Builder<FkTeam> {

        /**
         * Sets the name of the {@link FkTeam team}.
         *
         * @param name The name to set
         * @return This builder
         */
        @Contract("_ -> this")
        @NotNull Builder name(@NotNull String name);

        /**
         * Sets the color of the {@link FkTeam team}.
         *
         * @param color The color to set
         * @return This builder
         */
        @Contract("_ -> this")
        @NotNull Builder color(@NotNull TextColor color);

        /**
         * Sets the name displayed to users for the {@link FkTeam team}.
         *
         * <p>By default, this is set to {@link #name(String)} and {@link #color(TextColor)}.</p>
         *
         * @param displayName The component to set
         * @return This builder
         */
        @Contract("_ -> this")
        @NotNull Builder displayName(@NotNull Component displayName);

        @Contract("_ -> this")
        default @NotNull Builder playerNames(@NotNull String... names) {
            return this.playerNames(Arrays.asList(names));
        }

        @Contract("_ -> this")
        @NotNull Builder playerNames(@NotNull Iterable<String> names);

        @Contract("_ -> this")
        default @NotNull Builder playerUniqueIds(@NotNull UUID... uuids) {
            return this.playerUniqueIds(Arrays.asList(uuids));
        }

        @Contract("_ -> this")
        @NotNull Builder playerUniqueIds(@NotNull Iterable<UUID> uuids);
    }
}
