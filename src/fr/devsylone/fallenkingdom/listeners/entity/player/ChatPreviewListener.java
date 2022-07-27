package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent;

public class ChatPreviewListener implements Listener {

    @SuppressWarnings("deprecation") // Here be dragons
    @EventHandler
    public void onPreview(AsyncPlayerChatPreviewEvent event) {
        ChatListener.handleChat(event, false);
    }
}
