package fr.devsylone.fkpi.api;

import fr.devsylone.fkpi.teams.Base;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * Définit une équipe du Fallen Kingdom pour l'API
 */
public interface ITeam
{
    void addPlayer(String player);

    void removePlayer(String player);

    Base getBase();

    ChatColor getChatColor();

    String getName();

    List<String> getPlayers();

    String toString();
}
