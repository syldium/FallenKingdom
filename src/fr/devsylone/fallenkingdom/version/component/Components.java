package fr.devsylone.fallenkingdom.version.component;

import com.google.common.collect.Sets;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class Components {

    static final Set<ChatColor> TEXT_DECORATIONS = Sets.newHashSet(ChatColor.MAGIC, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.ITALIC, ChatColor.RESET);

    private static final boolean ADVENTURE;

    static {
        boolean adventure = false;
        try {
            BookMeta.class.isAssignableFrom(Book.class);
            adventure = true;
        } catch (Throwable ignored) { }
        ADVENTURE = adventure;
    }

    @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
    public static @NotNull BookMeta setBook(BookMeta meta, FkComponent title, FkComponent author, FkComponent... pages) {
        if (ADVENTURE) {
            // Utiliser un builder reviendrait à obtenir une instance de CraftMetaBook plutôt que de CraftMetaBookSigned, qui utilise la représentation legacy plutôt que json.
            meta.title(((AdventureImpl) title).component);
            meta.author(((AdventureImpl) author).component);
            meta.addPages(Arrays.stream(pages).map(component -> ((AdventureImpl) component).component).toArray(Component[]::new));
            return meta;
        }
        meta.setTitle(((BungeeImpl) title).component.toLegacyText());
        meta.setAuthor(((BungeeImpl) author).component.toLegacyText());
        meta.spigot().setPages(Arrays.stream(pages).map(component -> new BaseComponent[]{((BungeeImpl) component).component}).collect(Collectors.toList()));
        return meta;
    }

    static @NotNull FkComponent space() {
        if (ADVENTURE) {
            return AdventureImpl.SPACE;
        }
        return new BungeeImpl(" ");
    }

    static @NotNull FkComponent join(FkComponent... components) {
        if (ADVENTURE) {
            return new AdventureImpl(components);
        }
        return new BungeeImpl(components);
    }

    static @NotNull FkComponent text(String content, ChatColor... colors) {
        if (ADVENTURE) {
            return new AdventureImpl(content, colors);
        }
        return new BungeeImpl(content, colors);
    }
}
