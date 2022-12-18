package fr.devsylone.fallenkingdom.version.title;

import org.bukkit.entity.Player;

public class BukkitTitleSender implements TitleSender {
    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
