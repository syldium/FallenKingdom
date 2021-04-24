package fr.devsylone.fallenkingdom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface UUIDService {

    @Nullable String playerName(@NotNull UUID playerUniqueId);

    @Nullable UUID playerUniqueId(@NotNull String playerName);
}
