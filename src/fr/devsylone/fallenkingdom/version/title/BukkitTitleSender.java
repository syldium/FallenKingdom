package fr.devsylone.fallenkingdom.version.title;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitTitleSender implements TitleSender {
    @Override
    public void sendTitle(@NotNull Player player, @NotNull String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
