package fr.devsylone.fkpi.team;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface TeamManager {

    /**
     * Gets the {@link FkTeam team} by name, if any.
     *
     * @param name The team name
     * @return The team, if any
     */
    @NotNull Optional<@NotNull FkTeam> find(@NotNull String name);

    /**
     * Registers a new team.
     *
     * @param team A team
     * @return {@code true} if the team has been added
     * @throws InTooManyTeamsException If players in the team belong to a team already registered
     */
    boolean register(@NotNull FkTeam team);

    /**
     * Unregisters a team.
     *
     * @param team The team to unregister
     * @return Whether the team was registered
     */
    boolean unregister(@NotNull FkTeam team);

    /**
     * Return the player's team, if any.
     *
     * @param playerUniqueId The player's unique identifier
     * @return The player's team, if any
     */
    @NotNull Optional<@NotNull FkTeam> playerTeam(@NotNull UUID playerUniqueId);

    /**
     * Changes the player's team.
     * <p>
     * Unlike {@link FkTeam#addPlayer(UUID)}}, this method removes the player from his/her current team if he/she has one.
     *
     * @param playerUniqueId The player's unique identifier
     * @param team The player's new team
     * @return The result of the team change
     */
    @NotNull TeamChangeResult changePlayerTeam(@NotNull UUID playerUniqueId, @NotNull FkTeam team);

    /**
     * Gets an collection containing all teams.
     *
     * @return An unmodifiable collection
     */
    @NotNull @UnmodifiableView Collection<@NotNull FkTeam> teams();

    /**
     * Gets a {@link Stream} of all teams.
     *
     * @return The stream
     */
    @NotNull Stream<FkTeam> stream();

    /**
     * Returns a new team builder.
     *
     * @return A new team builder
     */
    @NotNull FkTeam.Builder teamBuilder();

    /**
     * Returns a new team builder and defines a name.
     *
     * @return A new team builder
     */
    default @NotNull FkTeam.Builder teamBuilder(@NotNull String name) {
        return this.teamBuilder().name(name);
    }
}
