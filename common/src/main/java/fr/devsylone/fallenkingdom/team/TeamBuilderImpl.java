package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fallenkingdom.UUIDService;
import fr.devsylone.fkpi.team.FkTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

class TeamBuilderImpl implements FkTeam.Builder {

    @VisibleForTesting
    static TextColor DEFAULT_COLOR = NamedTextColor.WHITE;

    private @Nullable String name;
    private TextColor color = DEFAULT_COLOR;
    private @Nullable Component displayName;
    private Iterable<String> playerNames;
    private Iterable<UUID> playerUniqueIds;

    private final UUIDService uuidService;

    TeamBuilderImpl(@NotNull UUIDService uuidService) {
        this.uuidService = uuidService;
        this.playerNames = Collections.emptySet();
        this.playerUniqueIds = Collections.emptySet();
    }

    TeamBuilderImpl(@NotNull TextColor color, @NotNull String name, @Nullable Component displayName, @NotNull Map<UUID, String> players, @NotNull UUIDService uuidService) {
        this.color = color;
        this.name = name;
        this.displayName = displayName;
        this.playerNames = Collections.emptySet();
        this.playerUniqueIds = players.keySet();
        this.uuidService = uuidService;
    }

    @Override
    public FkTeam.@NotNull Builder name(@NotNull String name) {
        this.name = requireNonNull(name, "name");
        return this;
    }

    @Override
    public FkTeam.@NotNull Builder color(@NotNull TextColor color) {
        this.color = requireNonNull(color, "color");
        return this;
    }

    @Override
    public FkTeam.@NotNull Builder displayName(@NotNull Component displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public FkTeam.@NotNull Builder playerNames(@NotNull Iterable<String> names) {
        this.playerNames = names;
        return this;
    }

    @Override
    public FkTeam.@NotNull Builder playerUniqueIds(@NotNull Iterable<UUID> uuids) {
        this.playerUniqueIds = uuids;
        return this;
    }

    @Override
    public @NotNull FkTeam build() {
        requireNonNull(this.name, "name is not set");
        final Component displayName = this.displayName == null ? Component.text(this.name, this.color) : this.displayName;
        final FkTeam team = new FkTeamImpl(this.color, this.name, displayName, this.uuidService);
        for (UUID playerUniqueId : this.playerUniqueIds) {
            team.addPlayer(playerUniqueId);
        }
        for (String playerName : this.playerNames) {
            team.addPlayer(playerName);
        }
        return team;
    }
}
