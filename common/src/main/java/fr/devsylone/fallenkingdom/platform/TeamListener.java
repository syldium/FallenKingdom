package fr.devsylone.fallenkingdom.platform;

import fr.devsylone.fkpi.team.FkTeam;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface TeamListener {

    void onRegister(@NotNull FkTeam team);

    void onUnregister(@NotNull FkTeam team);

    void onColorChange(@NotNull FkTeam team, @NotNull TextColor actual, @NotNull TextColor next);

    void onPlayerAdd(@NotNull FkTeam team, @NotNull String playerName, @NotNull UUID playerUniqueId);

    void onPlayerRemove(@NotNull FkTeam team, @NotNull String playerName, @NotNull UUID playerUniqueId);
}
