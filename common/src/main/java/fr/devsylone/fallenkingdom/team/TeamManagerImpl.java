package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fallenkingdom.UUIDService;
import fr.devsylone.fallenkingdom.platform.TeamListener;
import fr.devsylone.fkpi.team.FkTeam;
import fr.devsylone.fkpi.team.InTooManyTeamsException;
import fr.devsylone.fkpi.team.TeamChangeResult;
import fr.devsylone.fkpi.team.TeamManager;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class TeamManagerImpl implements TeamManager, TeamBridge {

    private final Map<String, FkTeam> teams = new ConcurrentHashMap<>();
    private final Map<UUID, FkTeam> playerTeam = new ConcurrentHashMap<>();

    private final TeamListener listener;
    private final UUIDService uuidService;

    public TeamManagerImpl(@NotNull TeamListener listener, @NotNull UUIDService uuidService) {
        this.listener = listener;
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
            ((FkTeamImpl) team).setBridge(this);
            for (UUID uuid : team.playersUniqueIds()) {
                this.playerTeam.put(uuid, team);
            }
            this.listener.onRegister(team);
        }
        return registered;
    }

    @Override
    public boolean unregister(@NotNull FkTeam team) {
        FkTeam removed = this.teams.remove(team.name());
        if (removed != null) {
            ((FkTeamImpl) team).setBridge(TeamBridge.ALWAYS_TRUE);
            team.playersUniqueIds().forEach(this.playerTeam::remove);
            this.listener.onUnregister(team);
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
    public void onColorChange(@NotNull FkTeam team, @NotNull TextColor actual, @NotNull TextColor next) {
        this.listener.onColorChange(team, actual, next);
    }

    @Override
    public @NotNull TeamChangeResult onPlayerAdd(@NotNull FkTeam team, @NotNull String playerName, @NotNull UUID playerUniqueId) {
        final FkTeam actual = this.playerTeam.get(playerUniqueId);
        if (actual != null && actual != team) {
            return TeamChangeResult.inTooManyTeams();
        }
        this.playerTeam.put(playerUniqueId, team);
        this.listener.onPlayerAdd(team, playerName, playerUniqueId);
        return TeamChangeResult.success();
    }

    @Override
    public boolean onPlayerRemove(@NotNull FkTeam team, @NotNull String playerName, @NotNull UUID playerUniqueId) {
        this.playerTeam.remove(playerUniqueId);
        this.listener.onPlayerRemove(team, playerName, playerUniqueId);
        return true;
    }

    @Override
    public @NotNull Iterable<FkTeam> audiences() {
        return this.playerTeam.values();
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
