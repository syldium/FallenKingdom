package fr.devsylone.fallenkingdom.version.packet.entity;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Une {@link org.bukkit.boss.BossBar} aussi utilisable en 1.8.
 */
public interface BossBar {

    BossBar.Factory INSTANCE = Provider.BOSS_BAR;

    /**
     * Ajoute un joueur qui verra la barre de boss.
     * <p>
     * Si le joueur suit déjà la barre, cette méthode n'a aucun effet.
     *
     * @param player le joueur
     */
    void addPlayer(@NotNull Player player);

    /**
     * Retire un joueur qui ne verra plus la barre de boss.
     *
     * @param player le joueur
     */
    void removePlayer(@NotNull Player player);

    /**
     * Modifie la progression de la barre de boss.
     *
     * @param progress la progression, entre 0.0 et 1.0
     */
    void setProgress(double progress);

    /**
     * Récupère la liste des joueurs qui voient la barre de boss.
     *
     * @return la liste des joueurs
     */
    @NotNull Collection<@NotNull Player> getPlayers();

    /**
     * Retire la barre de boss de tous les joueurs.
     */
    void removeAll();

    interface Factory {
        @NotNull BossBar createBossBar(@NotNull String name, @NotNull ChatColor color);
    }
}
