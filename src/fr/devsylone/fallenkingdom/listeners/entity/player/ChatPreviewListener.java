package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import fr.devsylone.fallenkingdom.chat.BuiltInChatListener;
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent;

public class ChatPreviewListener implements Listener {

    @SuppressWarnings("deprecation") // Here be dragons
    @EventHandler
    public void onPreview(AsyncPlayerChatPreviewEvent event) {
        new BuiltInChatListener().handleChat(event, false);
    }
}
