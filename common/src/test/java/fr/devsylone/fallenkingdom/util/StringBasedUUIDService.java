package fr.devsylone.fallenkingdom.util;

import fr.devsylone.fallenkingdom.UUIDService;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StringBasedUUIDService implements UUIDService {

    private static final String[] NAMES = new String[]{"Steve", "Alex", "Enderboy", "Pufferfish", "Sandcastle", "Piglin"};

    private final Map<UUID, String> uuidToString = new HashMap<>();
    private final Map<String, UUID> stringToUUID = new HashMap<>();

    @Override
    public @NotNull String playerName(@NotNull UUID playerUniqueId) {
        String playerName = this.uuidToString.get(playerUniqueId);
        if (playerName != null) {
            return playerName;
        }
        playerName = NAMES[this.uuidToString.size() % NAMES.length];
        this.store(playerName, playerUniqueId);
        return playerName;
    }

    @Override
    public @NotNull UUID playerUniqueId(@NotNull String playerName) {
        UUID playerUniqueId = this.stringToUUID.get(playerName);
        if (playerUniqueId != null) {
            return playerUniqueId;
        }
        playerUniqueId = this.generateUniqueId();
        this.store(playerName, playerUniqueId);
        return playerUniqueId;
    }

    private @NotNull UUID generateUniqueId() {
        long bits = 0;
        while (true) {
            UUID uuid = new UUID(0, bits++);
            if (!this.uuidToString.containsKey(uuid)) {
                return uuid;
            }
        }
    }

    private void store(@NotNull String playerName, @NotNull UUID playerUniqueId) {
        this.uuidToString.put(playerUniqueId, playerName);
        this.stringToUUID.put(playerName, playerUniqueId);
    }
}
