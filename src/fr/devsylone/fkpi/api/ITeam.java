package fr.devsylone.fkpi.api;

import fr.devsylone.fkpi.teams.Base;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

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
    void addPlayer(@NotNull String player);

    void removePlayer(@NotNull String player);

    @Nullable Base getBase();

    @NotNull ChatColor getChatColor();

    @NotNull DyeColor getDyeColor();

    void setName(@NotNull String name);

    @NotNull String getName();

    @UnmodifiableView List<String> getPlayers();
}
