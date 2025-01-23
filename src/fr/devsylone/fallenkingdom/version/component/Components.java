package fr.devsylone.fallenkingdom.version.component;

import com.google.common.collect.Sets;
import net.kyori.adventure.inventory.Book;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

final class Components {

    static final Set<ChatColor> TEXT_DECORATIONS = Sets.newHashSet(ChatColor.MAGIC, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.ITALIC, ChatColor.RESET);

    static final boolean ADVENTURE;
    static final boolean ADVENTURE_BOOK;

    static {
        boolean adventure = false;
        try {
            Class.forName("net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer");
            adventure = true;
        } catch (ClassNotFoundException ignored) { }
        ADVENTURE = adventure;

        boolean adventureBook = false;
        try {
            Player.class.getMethod("openBook", Book.class);
            adventureBook = true;
        } catch (NoSuchMethodException ignored) { }
        ADVENTURE_BOOK = adventureBook;
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
