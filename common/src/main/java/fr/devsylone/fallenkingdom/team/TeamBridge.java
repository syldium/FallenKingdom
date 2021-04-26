package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fkpi.team.FkTeam;
import fr.devsylone.fkpi.team.TeamChangeResult;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

interface TeamBridge {

    TeamBridge ALWAYS_TRUE = new TeamBridge() {
        @Override
        public @NotNull TeamChangeResult onPlayerAdd(@NotNull FkTeam team, @NotNull UUID playerUniqueId) {
            return TeamChangeResult.success();
        }

        @Override
        public boolean onPlayerRemove(@NotNull FkTeam team, @NotNull UUID playerUniqueId) {
            return true;
        }
    };

    @NotNull TeamChangeResult onPlayerAdd(@NotNull FkTeam team, @NotNull UUID playerUniqueId);

    boolean onPlayerRemove(@NotNull FkTeam team, @NotNull UUID playerUniqueId);
}
