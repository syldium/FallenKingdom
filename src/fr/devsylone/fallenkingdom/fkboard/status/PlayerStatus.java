package fr.devsylone.fallenkingdom.fkboard.status;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;

public class PlayerStatus {

    private final Map<String, Boolean> statutes = new ConcurrentHashMap<>();

    public void update(@Nonnull String playerName, boolean online) {
        statutes.put(playerName, online);
    }

    public boolean isPlayerOnline(@Nonnull String playerName) {
        return statutes.computeIfAbsent(playerName, k -> Bukkit.getPlayer(playerName) != null);
    }
}
