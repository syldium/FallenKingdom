package fr.devsylone.fallenkingdom.version.component;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fr.devsylone.fallenkingdom.version.component.Components.TEXT_DECORATIONS;
import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;

class AdventureImpl implements FkComponent {

    Component component;

    AdventureImpl(@NotNull Component component) {
        this.component = component;
    }

    AdventureImpl(String content, ChatColor... colors) {
        this(Component.text(content, asAdventure(colors)));
    }

    AdventureImpl(FkComponent... components) {
        this(Component.join(Component.empty(), asAdventure(components)));
    }

    static @NotNull FkComponent newline() {
        return new AdventureImpl(Component.newline());
    }

    static @NotNull FkComponent space() {
        return new AdventureImpl(Component.space());
    }

    @Override
    public @NotNull FkComponent changePage(int page) {
        this.component = this.component.clickEvent(ClickEvent.changePage(page));
        return this;
    }

    @Override
    public @NotNull FkComponent command(@NotNull String command) {
        this.component = this.component.clickEvent(ClickEvent.runCommand(command));
        return this;
    }

    @Override
    public @NotNull FkComponent interact(@NotNull net.md_5.bungee.api.chat.ClickEvent event) {
        this.component = this.component.clickEvent(ClickEvent.clickEvent(asAdventure(event.getAction()), event.getValue()));
        return this;
    }

    @Override
    public @NotNull FkComponent hover(@NotNull String content) {
        this.component = this.component.hoverEvent(HoverEvent.showText(Component.text(content)));
        return this;
    }

    @Override
    public @NotNull FkComponent hover(@NotNull FkComponent component) {
        this.component = this.component.hoverEvent(HoverEvent.showText(((AdventureImpl) component).component));
        return this;
    }

    @Override
    public @NotNull String toLegacyText() {
        return legacySection().serialize(this.component);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AdventureImpl)) return false;
        AdventureImpl adventure = (AdventureImpl) o;
        return this.component.equals(adventure.component);
    }

    @Override
    public int hashCode() {
        return this.component.hashCode();
    }

    @Override
    public String toString() {
        return this.component.toString();
    }

    private static @NotNull Action asAdventure(net.md_5.bungee.api.chat.ClickEvent.Action eventAction) {
        switch (eventAction) {
            case OPEN_URL: return Action.OPEN_URL;
            case OPEN_FILE: return Action.OPEN_FILE;
            case RUN_COMMAND: return Action.RUN_COMMAND;
            case SUGGEST_COMMAND: return Action.SUGGEST_COMMAND;
            case CHANGE_PAGE: return Action.CHANGE_PAGE;
            case COPY_TO_CLIPBOARD: return Action.COPY_TO_CLIPBOARD;
            default: throw new IllegalArgumentException("Unknown click event.");
        }
    }

    private static @NotNull Style asAdventure(ChatColor... colors) {
        if (colors.length == 0) return Style.empty();
        if (colors.length == 1 && !TEXT_DECORATIONS.contains(colors[0])) return Style.style(asAdventure(colors[0]));
        Style.Builder style = Style.style();
        for (ChatColor color : colors) {
            if (color == ChatColor.MAGIC) style.decorate(TextDecoration.OBFUSCATED);
            else if (color == ChatColor.BOLD) style.decorate(TextDecoration.BOLD);
            else if (color == ChatColor.STRIKETHROUGH) style.decorate(TextDecoration.STRIKETHROUGH);
            else if (color == ChatColor.UNDERLINE) style.decorate(TextDecoration.UNDERLINED);
            else if (color == ChatColor.ITALIC) style.decorate(TextDecoration.ITALIC);
            else if (color != ChatColor.RESET) style.color(asAdventure(color));
        }
        return style.build();
    }

    private static @NotNull TextColor asAdventure(ChatColor color) {
        if (color == ChatColor.BLACK) return NamedTextColor.BLACK;
        if (color == ChatColor.DARK_BLUE) return NamedTextColor.DARK_BLUE;
        if (color == ChatColor.DARK_GREEN) return NamedTextColor.DARK_GREEN;
        if (color == ChatColor.DARK_AQUA) return NamedTextColor.DARK_AQUA;
        if (color == ChatColor.DARK_RED) return NamedTextColor.DARK_RED;
        if (color == ChatColor.DARK_PURPLE) return NamedTextColor.DARK_PURPLE;
        if (color == ChatColor.GOLD) return NamedTextColor.GOLD;
        if (color == ChatColor.GRAY) return NamedTextColor.GRAY;
        if (color == ChatColor.DARK_GRAY) return NamedTextColor.DARK_GRAY;
        if (color == ChatColor.BLUE) return NamedTextColor.BLUE;
        if (color == ChatColor.GREEN) return NamedTextColor.GREEN;
        if (color == ChatColor.AQUA) return NamedTextColor.AQUA;
        if (color == ChatColor.RED) return NamedTextColor.RED;
        if (color == ChatColor.LIGHT_PURPLE) return NamedTextColor.LIGHT_PURPLE;
        if (color == ChatColor.WHITE) return NamedTextColor.WHITE;
        return TextColor.color(color.getColor().getRGB());
    }

    static @NotNull Component asAdventure(FkComponent component) {
        return ((AdventureImpl) component).component;
    }

    static @NotNull Component[] asAdventure(FkComponent... components) {
        return Arrays.stream(components).map(AdventureImpl::asAdventure).toArray(Component[]::new);
    }

    static class BookImpl implements FkBook {

        private final Book book;
        private final FkComponent title;
        private final FkComponent author;
        private final List<FkComponent> pages;
        private ItemStack itemStack;

        BookImpl(FkComponent title, FkComponent author, FkComponent... pages) {
            // Le titre est limité à 32 caractères, ce qui est facilement dépassable puisque le titre sera ici converti en json.
            // Comme il ne sera au final pas affiché, il est défini vide.
            // Note: avec BookMeta, le titre est converti en chaîne legacy.
            this.book = Book.book(Component.empty(), asAdventure(author), asAdventure(pages));
            this.title = title;
            this.author = author;
            this.pages = Arrays.asList(pages);
        }

        @Override
        public @NotNull FkComponent title() {
            return this.title;
        }

        @Override
        public @NotNull FkComponent author() {
            return this.author;
        }

        @Override
        public @NotNull List<FkComponent> pages() {
            return this.pages;
        }

        @Override
        public @NotNull String jsonPages() {
            return gson().serializer().toJson(
                    this.book.pages().stream().map(page -> gson().serialize(page)).collect(Collectors.toList())
            );
        }

        @Override
        public @NotNull ItemStack asItemStack() {
            if (this.itemStack == null) {
                this.itemStack = new ItemStack(Material.WRITTEN_BOOK);
                final BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
                meta.title(this.book.title());
                meta.author(this.book.author());
                meta.addPages(this.book.pages().toArray(new Component[0]));
                this.itemStack.setItemMeta(meta);
            }
            return this.itemStack;
        }

        @Override
        public void open(@NotNull Player player) {
            player.openBook(this.book);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BookImpl)) return true;
            BookImpl book1 = (BookImpl) o;
            return this.book.equals(book1.book);
        }

        @Override
        public int hashCode() {
            return this.book.hashCode();
        }

        @Override
        public String toString() {
            return this.book.toString();
        }
    }
}
