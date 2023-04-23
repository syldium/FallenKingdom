package fr.devsylone.fallenkingdom.version.title;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TitleSender {

    TitleSender INSTANCE = Provider.TITLE_SENDER;

    void sendTitle(@NotNull Player player, @NotNull String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut);
}
