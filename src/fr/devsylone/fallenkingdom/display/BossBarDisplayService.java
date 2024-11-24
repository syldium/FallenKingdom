package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.display.content.Content;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.jetbrains.annotations.NotNull;

abstract class BossBarDisplayService extends SimpleDisplayService {

    BossBarDisplayService(@NotNull Content content) {
        super(DisplayType.BOSSBAR, content);
    }

    abstract @NotNull BarColor color();

    abstract void withColor(@NotNull BarColor color);

    abstract @NotNull BarStyle style();

    abstract void withStyle(@NotNull BarStyle style);
}
