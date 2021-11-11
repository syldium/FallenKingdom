package fr.devsylone.fallenkingdom.display.progress;

import fr.devsylone.fallenkingdom.utils.KeyHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.devsylone.fallenkingdom.utils.ConfigHelper.enumValueOf;

class BossBarProgress extends AbstractProgressBar {

    private final KeyedBossBar bar;

    BossBarProgress(AbstractProgressBar.@NotNull ProviderImpl provider, @NotNull KeyedBossBar bar) {
        super(provider);
        this.bar = bar;
    }

    @Override
    public void progress(@NotNull Player player, @NotNull Location location, double progress) {
        this.bar.setProgress(progress);
        this.bar.setTitle(this.formatText(progress));
    }

    @Override
    public void remove(@NotNull Player player) {
        this.bar.removePlayer(player);
        Bukkit.removeBossBar(this.bar.getKey());
    }

    static class ProviderImpl extends AbstractProgressBar.ProviderImpl {

        private final BarColor color;
        private final BarStyle style;

        private static final String COLOR = "color";
        private static final String STYLE = "style";

        ProviderImpl(@NotNull ConfigurationSection config) {
            super(config);
            this.color = enumValueOf(BarColor.class, config.getString(COLOR), BarColor.WHITE);
            this.style = enumValueOf(BarStyle.class, config.getString(STYLE), BarStyle.SOLID);
        }

        @Override
        public @NotNull ProgressBar init(@NotNull Player player, @NotNull Location location) {
            final KeyedBossBar bar = Bukkit.createBossBar(KeyHelper.plugin("progress_" + player.getName()), "", this.color, this.style);
            bar.setVisible(true);
            bar.addPlayer(player);
            return new BossBarProgress(this, bar);
        }

        @Override
        public void save(@NotNull ConfigurationSection config) {
            super.save(config);
            config.set(COLOR, this.color.name());
            config.set(STYLE, this.style.name());
        }

        @Override
        @NotNull String type() {
            return BOSSBAR;
        }
    }
}
