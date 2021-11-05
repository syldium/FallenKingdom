package fr.devsylone.fallenkingdom.display.progress;

import fr.devsylone.fallenkingdom.Fk;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class HologramProgress extends AbstractProgressBar {

    private final int entityId;

    HologramProgress(AbstractProgressBar.@NotNull ProviderImpl provider, @NotNull Player player, @NotNull Location location) {
        super(provider);
        this.entityId = Fk.getInstance().getPacketManager().createFloatingText(this.formatText(0D), player, location);
    }

    @Override
    public void progress(@NotNull Player player, @NotNull Location location, double progress) {
        Fk.getInstance().getPacketManager().updateFloatingText(this.entityId, this.formatText(progress));
    }

    @Override
    public void remove(@NotNull Player player) {
        Fk.getInstance().getPacketManager().remove(this.entityId);
    }

    static class ProviderImpl extends AbstractProgressBar.ProviderImpl {

        ProviderImpl(@NotNull ConfigurationSection config) {
            super(config);
        }

        @Override
        @NotNull String type() {
            return HOLOGRAM;
        }

        @Override
        public @NotNull ProgressBar init(@NotNull Player player, @NotNull Location location) {
            return new HologramProgress(this, player, location);
        }
    }
}
