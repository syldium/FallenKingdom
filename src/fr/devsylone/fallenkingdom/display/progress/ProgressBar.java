package fr.devsylone.fallenkingdom.display.progress;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import static fr.devsylone.fallenkingdom.display.progress.AbstractProgressBar.ProviderImpl.ACTIONBAR;
import static fr.devsylone.fallenkingdom.display.progress.AbstractProgressBar.ProviderImpl.BOSSBAR;
import static fr.devsylone.fallenkingdom.display.progress.AbstractProgressBar.ProviderImpl.HOLOGRAM;

/**
 * Un média pour afficher une progression, généralement de 0 à 100%.
 */
public interface ProgressBar {

    ProgressBar EMPTY = new ProgressBar() {
        @Override
        public void progress(@NotNull Player player, @NotNull Location location, double progress) {

        }

        @Override
        public void remove(@NotNull Player player) {

        }
    };

    /**
     * Met à jour l'indication de progression.
     *
     * @param player Joueur associé
     * @param location Position s'il s'agit d'une entité
     * @param progress La progression, entre 0 et 1
     */
    void progress(@NotNull Player player, @NotNull Location location, @Range(from = 0, to = 1) double progress);

    /**
     * Retire l'indicateur de progression.
     *
     * @param player Joueur associé
     */
    void remove(@NotNull Player player);

    /**
     * Formate une valeur de progression en un pourcentage.
     *
     * @param progress La progression
     * @return Pourcentage formaté
     */
    static @NotNull String percent(double progress) {
        return String.valueOf((int) (progress * 100));
    }

    @FunctionalInterface
    interface Provider {

        Provider EMPTY = (player, location) -> ProgressBar.EMPTY;

        /**
         * Initialise une barre de progression pour un joueur et à endroit donné.
         *
         * @param player Joueur associé
         * @param location Position s'il s'agit d'une entité
         * @return La barre de progression initialisée
         */
        @NotNull ProgressBar init(@NotNull Player player, @NotNull Location location);

        default void save(@NotNull ConfigurationSection config) {

        }

        static @NotNull ProgressBar.Provider fromConfig(@Nullable ConfigurationSection config) {
            if (config == null) {
                return new HologramProgress.ProviderImpl(new MemoryConfiguration());
            }
            final String type = config.getString("type", HOLOGRAM);
            if (ACTIONBAR.equals(type)) {
                return new ActionBarProgress.ProviderImpl(config);
            } else if (BOSSBAR.equals(type)) {
                return new BossBarProgress.ProviderImpl(config);
            } else {
                return new HologramProgress.ProviderImpl(config);
            }
        }
    }
}
