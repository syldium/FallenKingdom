package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fkpi.team.FkTeam;
import fr.devsylone.fkpi.team.TeamChangeResult;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

interface TeamBridge {

    TeamBridge ALWAYS_TRUE = new TeamBridge() {
        @Override
        public void onColorChange(@NotNull FkTeam team, @NotNull TextColor actual, @NotNull TextColor next) {

        }

        @Override
        public @NotNull TeamChangeResult onPlayerAdd(@NotNull FkTeam team, @NotNull String playerName, @NotNull UUID playerUniqueId) {
            return TeamChangeResult.success();
        }

        @Override
        public boolean onPlayerRemove(@NotNull FkTeam team, @NotNull String playerName, @NotNull UUID playerUniqueId) {
            return true;
        }
    };

    void onColorChange(@NotNull FkTeam team, @NotNull TextColor actual, @NotNull TextColor next);

    @NotNull TeamChangeResult onPlayerAdd(@NotNull FkTeam team, @NotNull String playerName, @NotNull UUID playerUniqueId);

    boolean onPlayerRemove(@NotNull FkTeam team, @NotNull String playerName, @NotNull UUID playerUniqueId);
}
