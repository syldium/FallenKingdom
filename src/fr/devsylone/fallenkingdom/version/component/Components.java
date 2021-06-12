package fr.devsylone.fallenkingdom.version.component;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import fr.devsylone.fallenkingdom.version.Environment;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Components {

    static final Set<ChatColor> TEXT_DECORATIONS = Sets.newHashSet(ChatColor.MAGIC, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.ITALIC, ChatColor.RESET);

    private static final Pattern CHANGE_PAGE_FIX;
    private static final boolean ADVENTURE;
    private static final boolean HAS_CHANGE_PAGE;

    static {
        boolean adventure = false;
        try {
            BookMeta.class.isAssignableFrom(Book.class);
            adventure = true;
        } catch (Throwable ignored) { }
        ADVENTURE = adventure;

        boolean changePage = false;
        try {
            ClickEvent.Action.valueOf("CHANGE_PAGE");
            changePage = true;
        } catch (IllegalArgumentException ignored) { /* WHY ??? */ }
        HAS_CHANGE_PAGE = changePage;
        CHANGE_PAGE_FIX = changePage ? null : Pattern.compile("\\\\\"action\\\\\":\\\\\"run_command\\\\\",\\\\\"value\\\\\":\\\\\"\\/change_page (\\d+)\\\\\"");
    }

    public static boolean isChangePageSupported() {
        return HAS_CHANGE_PAGE;
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
        if (Environment.hasSpigotBookPages()) {
            meta.spigot().setPages(Arrays.stream(pages).map(component -> new BaseComponent[]{((BungeeImpl) component).component}).collect(Collectors.toList()));
        }
        return meta;
    }

    public static @NotNull String stringifyBook(FkComponent title, FkComponent author, FkComponent... pages) {
        final String legacyTitle = ((BungeeImpl) title).component.toLegacyText();
        final String legacyAuthor = ((BungeeImpl) author).component.toLegacyText();
        final List<String> jsonPages = Arrays.stream(pages).map(component -> ComponentSerializer.toString(((BungeeImpl) component).component)).collect(Collectors.toList());
        final String pagesNbt = fixChangePage(new Gson().toJson(jsonPages));
        return "{pages:" + pagesNbt + ",title:\"" + legacyTitle + "\",author:\"" + legacyAuthor + "\"}";
    }

    static @NotNull String fixChangePage(@NotNull String jsonRepresentation) {
        if (HAS_CHANGE_PAGE) return jsonRepresentation;
        return CHANGE_PAGE_FIX.matcher(jsonRepresentation).replaceAll("\\\\\"action\\\\\":\\\\\"change_page\\\\\",\\\\\"value\\\\\":$1");
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
