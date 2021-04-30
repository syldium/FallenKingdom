package fr.devsylone.fallenkingdom;

import fr.devsylone.fkpi.team.FkTeam;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface UUIDService {

    @Nullable String playerName(@NotNull UUID playerUniqueId);

    @Nullable UUID playerUniqueId(@NotNull String playerName);

    @NotNull Audience teamAudience(@NotNull FkTeam team);

    @NotNull Audience playerAudience(@NotNull UUID playerUniqueId);
}
