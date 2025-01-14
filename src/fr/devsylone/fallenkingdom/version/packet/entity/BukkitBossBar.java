package fr.devsylone.fallenkingdom.version.packet.entity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class BukkitBossBar implements BossBar {

    private final org.bukkit.boss.BossBar bar;

    BukkitBossBar(@NotNull String name, @NotNull ChatColor color) {
        this.bar = Bukkit.createBossBar(name, as(color), BarStyle.SOLID);
        this.bar.setVisible(true);
    }

    @Override
    public void addPlayer(@NotNull Player player) {
        this.bar.addPlayer(player);
    }

    @Override
    public void removePlayer(@NotNull Player player) {
        this.bar.removePlayer(player);
    }

    @Override
    public void setProgress(double progress) {
        this.bar.setProgress(progress);
    }

    @Override
    public @NotNull List<@NotNull Player> getPlayers() {
        return this.bar.getPlayers();
    }

    @Override
    public void removeAll() {
        this.bar.removeAll();
    }

    private static @NotNull BarColor as(@NotNull ChatColor color) {
        switch (color) {
            case DARK_RED:
            case RED:
                return BarColor.RED;
            case DARK_GREEN:
            case GREEN:
                return BarColor.GREEN;
            case AQUA:
            case DARK_AQUA:
            case BLUE:
            case DARK_BLUE:
                return BarColor.BLUE;
            case LIGHT_PURPLE:
                return BarColor.PINK;
            case YELLOW:
            case GOLD:
                return BarColor.YELLOW;
            case DARK_PURPLE:
                return BarColor.PURPLE;
            default:
                return BarColor.WHITE;
        }
    }
}
