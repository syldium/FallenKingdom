package fr.devsylone.fallenkingdom.platform;

import fr.devsylone.fkpi.team.FkTeam;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TeamListenerMock implements TeamListener {

    private FkTeam lastRegister;
    private FkTeam lastUnregister;
    private FkTeam lastColorChange;
    private FkTeam lastPlayerAdd;
    private FkTeam lastPlayerRemove;

    @Override
    public void onRegister(@NotNull FkTeam team) {
        this.lastRegister = team;
    }

    public @Nullable FkTeam poolLastRegister() {
        final FkTeam team = this.lastRegister;
        this.lastRegister = null;
        return team;
    }

    @Override
    public void onUnregister(@NotNull FkTeam team) {
        this.lastUnregister = team;
    }

    public @Nullable FkTeam poolLastUnregister() {
        final FkTeam team = this.lastUnregister;
        this.lastUnregister = null;
        return team;
    }

    @Override
    public void onColorChange(@NotNull FkTeam team, @NotNull TextColor actual, @NotNull TextColor next) {
        this.lastColorChange = team;
    }

    @Override
    public void onPlayerAdd(@NotNull FkTeam team, @NotNull String playerName, @NotNull UUID playerUniqueId) {
        this.lastPlayerAdd = team;
    }

    @Override
    public void onPlayerRemove(@NotNull FkTeam team, @NotNull String playerName, @NotNull UUID playerUniqueId) {
        this.lastPlayerRemove = team;
    }
}
