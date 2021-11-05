package fr.devsylone.fallenkingdom.display.progress;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.devsylone.fallenkingdom.display.ActionBarDisplayService.SEND_ACTION_BAR;

class ActionBarProgress extends AbstractProgressBar {

    ActionBarProgress(AbstractProgressBar.@NotNull ProviderImpl provider) {
        super(provider);
    }

    @Override
    public void progress(@NotNull Player player, @NotNull Location location, double progress) {
        SEND_ACTION_BAR.accept(player, this.formatText(progress));
    }

    @Override
    public void remove(@NotNull Player player) {

    }

    static class ProviderImpl extends AbstractProgressBar.ProviderImpl {

        private final ProgressBar progress;

        ProviderImpl(@NotNull ConfigurationSection config) {
            super(config);
            this.progress = new ActionBarProgress(this);
        }

        @Override
        public @NotNull ProgressBar init(@NotNull Player player, @NotNull Location location) {
            return this.progress;
        }

        @Override
        @NotNull String type() {
            return BOSSBAR;
        }
    }
}
