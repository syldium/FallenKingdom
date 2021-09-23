package fr.devsylone.fallenkingdom.display;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.jetbrains.annotations.NotNull;

public abstract class BossBarDisplayService extends SimpleDisplayService {

    public BossBarDisplayService(@NotNull String value) {
        super(DisplayType.BOSSBAR, value);
    }

    public abstract @NotNull BarColor color();

    public abstract void withColor(@NotNull BarColor color);

    public abstract @NotNull BarStyle style();

    public abstract void withStyle(@NotNull BarStyle style);
}
