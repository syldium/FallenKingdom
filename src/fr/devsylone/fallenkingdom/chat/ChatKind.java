package fr.devsylone.fallenkingdom.chat;

import org.bukkit.event.Listener;
import lombok.Getter;

public enum ChatKind {
    BUILT_IN, CARBON;

    public Listener getListener() {
        switch (this) {
            case CARBON:
                return new CarbonChatListener();
            default:
                return new BuiltInChatListener();
        }
    }
}
