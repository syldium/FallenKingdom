package fr.devsylone.fallenkingdom.version.title;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface TitleSender {

    TitleSender INSTANCE = new BukkitTitleSender();

    void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut);
}
