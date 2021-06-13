package fr.devsylone.fallenkingdom.version.component;

import com.google.common.collect.Sets;
import net.kyori.adventure.inventory.Book;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

final class Components {

    static final Set<ChatColor> TEXT_DECORATIONS = Sets.newHashSet(ChatColor.MAGIC, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.ITALIC, ChatColor.RESET);

    static final boolean ADVENTURE;

    static {
        boolean adventure = false;
        try {
            BookMeta.class.isAssignableFrom(Book.class);
            adventure = true;
        } catch (Throwable ignored) { }
        ADVENTURE = adventure;
    }

    static @NotNull FkComponent newline() {
        return ADVENTURE ? AdventureImpl.newline() : BungeeImpl.newline();
    }

    static @NotNull FkComponent space() {
        return ADVENTURE ? AdventureImpl.space() : BungeeImpl.space();
    }

    static @NotNull FkComponent join(FkComponent... components) {
        return ADVENTURE ? new AdventureImpl(components) : new BungeeImpl(components);
    }

    static @NotNull FkComponent text(String content, ChatColor... colors) {
        return ADVENTURE ? new AdventureImpl(content, colors) : new BungeeImpl(content, colors);
    }
}
