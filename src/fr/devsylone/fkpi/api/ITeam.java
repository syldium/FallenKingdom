package fr.devsylone.fkpi.api;

import fr.devsylone.fkpi.teams.Base;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.List;

/**
 * Définit une équipe du Fallen Kingdom pour l'API.
 *
 * Toutes ces fonctions sont thread-safe. Après un changement sur la
 * liste des joueurs, appeler ScoreboardManager#refreshAllScoreboards()
 * sur le thread principal pour mettre à jour les scoreboards et pseudos.
 */
public interface ITeam
{
    void addPlayer(String player);

    void removePlayer(String player);

    Base getBase();

    ChatColor getChatColor();

    DyeColor getDyeColor();

    void setName(String name);

    String getName();

    List<String> getPlayers();

    String toString();
}
