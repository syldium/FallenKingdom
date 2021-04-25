package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fallenkingdom.UUIDService;
import fr.devsylone.fkpi.team.FkTeam;
import fr.devsylone.fkpi.team.InTooManyTeamsException;
import fr.devsylone.fkpi.team.TeamChangeResult;
import fr.devsylone.fkpi.team.TeamManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class TeamManagerImpl implements TeamManager, TeamListener {

    private final Map<String, FkTeam> teams = new ConcurrentHashMap<>();
    private final Map<UUID, FkTeam> playerTeam = new ConcurrentHashMap<>();

    private final UUIDService uuidService;

    public TeamManagerImpl(@NotNull UUIDService uuidService) {
        this.uuidService = uuidService;
    }

    @Override
    public @NotNull Optional<FkTeam> find(@NotNull String name) {
        return Optional.ofNullable(this.teams.get(name));
    }

    @Override
    public boolean register(@NotNull FkTeam team) {
        if (this.someHasTeam(team.playersUniqueIds())) {
            throw new InTooManyTeamsException();
        }

        final boolean registered = this.teams.putIfAbsent(team.name(), team) == null;
        if (registered) {
            ((FkTeamImpl) team).setListener(this);
            for (UUID uuid : team.playersUniqueIds()) {
                this.playerTeam.put(uuid, team);
            }
        }
        return registered;
    }

    @Override
    public boolean unregister(@NotNull FkTeam team) {
        FkTeam removed = this.teams.remove(team.name());
        if (removed != null) {
            ((FkTeamImpl) team).setListener(TeamListener.ALWAYS_TRUE);
            team.playersUniqueIds().forEach(this.playerTeam::remove);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull Collection<FkTeam> teams() {
        return Collections.unmodifiableCollection(this.teams.values());
    }

    @Override
    public @NotNull Stream<FkTeam> stream() {
        return this.teams.values().stream();
    }

    @Override
    public @NotNull Optional<@NotNull FkTeam> playerTeam(@NotNull UUID playerUniqueId) {
        return Optional.ofNullable(this.playerTeam.get(playerUniqueId));
    }

    @Override
    public @NotNull TeamChangeResult changePlayerTeam(@NotNull UUID playerUniqueId, @NotNull FkTeam team) {
        FkTeam previous = this.playerTeam.get(playerUniqueId);
        if (previous != null) {
            if (previous.equals(team)) {
                return TeamChangeResult.alreadyIn();
            }
            previous.removePlayer(playerUniqueId);
        }
        return team.addPlayer(playerUniqueId);
    }

    @Override
    public @NotNull FkTeam.Builder teamBuilder() {
        return new TeamBuilderImpl(this.uuidService);
    }

    @Override
    public @NotNull TeamChangeResult onPlayerAdd(@NotNull FkTeam team, @NotNull UUID playerUniqueId) {
        if (this.playerTeam.containsKey(playerUniqueId)) {
            return TeamChangeResult.inTooManyTeams();
        }
        this.playerTeam.put(playerUniqueId, team);
        return TeamChangeResult.success();
    }

    @Override
    public boolean onPlayerRemove(@NotNull FkTeam team, @NotNull UUID playerUniqueId) {
        this.playerTeam.remove(playerUniqueId);
        return true;
    }

    private boolean someHasTeam(@NotNull Iterable<@NotNull UUID> iterable) {
        for (UUID playerUniqueId : iterable) {
            if (this.playerTeam.containsKey(playerUniqueId)) {
                return true;
            }
        }
        return false;
    }

    @NotNull UUIDService uuidService() {
        return this.uuidService;
    }

    @Override
    public String toString() {
        return "TeamManager{" + this.teams + '}';
    }
}
