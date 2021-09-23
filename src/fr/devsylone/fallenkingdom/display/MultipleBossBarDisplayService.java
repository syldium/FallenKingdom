package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.KeyHelper;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class MultipleBossBarDisplayService extends BossBarDisplayService {

    private final Map<UUID, KeyedBossBar> bars;
    private BarColor color;
    private BarStyle style;

    public MultipleBossBarDisplayService(@NotNull String value, @NotNull BarColor color, @NotNull BarStyle style) {
        this(value, color, style, new HashMap<>());
    }

    public MultipleBossBarDisplayService(@NotNull String value, @NotNull BarColor color, @NotNull BarStyle style, @NotNull Map<UUID, KeyedBossBar> bars) {
        super(value);
        this.color = color;
        this.style = style;
        this.bars = bars;
    }

    @Override
    public void show(@NotNull Player player, @NotNull String message) {
        final BossBar existing = this.existingBar(player);
        if (existing == null) {
            final KeyedBossBar bar = Bukkit.createBossBar(KeyHelper.plugin(player.getName()), message, this.color, this.style);
            bar.setVisible(true);
            bar.setProgress(1.0D);
            bar.addPlayer(player);
            this.bars.put(player.getUniqueId(), bar);
        } else {
            existing.setTitle(message);
        }
    }

    @Override
    public void hide(@NotNull Player player, @NotNull FkPlayer fkPlayer) {
        final KeyedBossBar removed = this.bars.remove(player.getUniqueId());
        if (removed != null) {
            removed.removePlayer(player);
            Bukkit.removeBossBar(removed.getKey());
        }
    }

    @Override
    public @NotNull BarColor color() {
        return this.color;
    }

    @Override
    public void withColor(@NotNull BarColor color) {
        this.color = requireNonNull(color, "bar color");
        for (BossBar bar : this.bars.values()) {
            bar.setColor(color);
        }
    }

    @Override
    public @NotNull BarStyle style() {
        return this.style;
    }

    @Override
    public void withStyle(@NotNull BarStyle style) {
        this.style = requireNonNull(style, "bar style");
        for (BossBar bar : this.bars.values()) {
            bar.setStyle(style);
        }
    }

    @Override
    public @NotNull MultipleBossBarDisplayService withValue(@NotNull String next) {
        return new MultipleBossBarDisplayService(next, this.color, this.style, this.bars);
    }

    private @Nullable BossBar existingBar(@NotNull Player player) {
        return this.bars.get(player.getUniqueId());
    }
}
