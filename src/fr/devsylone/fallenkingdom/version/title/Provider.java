package fr.devsylone.fallenkingdom.version.title;

import org.bukkit.entity.Player;

final class Provider {

    static final TitleSender TITLE_SENDER;

    static {
        boolean api = false;
        try {
            Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            api = true;
        } catch (ReflectiveOperationException ignored) {}
        TITLE_SENDER = api ? new BukkitTitleSender() : new NMSTitleSender();
    }
}
