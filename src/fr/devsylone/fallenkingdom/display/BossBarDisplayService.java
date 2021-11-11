package fr.devsylone.fallenkingdom.display;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.jetbrains.annotations.NotNull;

abstract class BossBarDisplayService extends SimpleDisplayService {

    BossBarDisplayService(@NotNull String value) {
        super(DisplayType.BOSSBAR, value);
    }

    abstract @NotNull BarColor color();

    abstract void withColor(@NotNull BarColor color);

    abstract @NotNull BarStyle style();

    abstract void withStyle(@NotNull BarStyle style);
}
