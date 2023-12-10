package fr.devsylone.fallenkingdom.version.component;

import com.google.gson.Gson;
import fr.devsylone.fallenkingdom.version.Environment;
import fr.devsylone.fallenkingdom.version.packet.book.BookViewer;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class BukkitImpl {

    static class BookImpl implements FkBook {

        static final Pattern CHANGE_PAGE_FIX;

        static {
            boolean changePage = false;
            try {
                ClickEvent.Action.valueOf("CHANGE_PAGE");
                changePage = true;
            } catch (IllegalArgumentException ignored) { /* WHY ??? */ }
            CHANGE_PAGE_FIX = changePage ? null : Pattern.compile("\\\\\"action\\\\\":\\\\\"run_command\\\\\",\\\\\"value\\\\\":\\\\\"\\/change_page (\\d+)\\\\\"");
        }

        private final ItemStack itemStack;
        private final FkComponent title;
        private final FkComponent author;
        private final List<FkComponent> pages;
        private String nbt;

        BookImpl(FkComponent title, FkComponent author, FkComponent[] pages) {
            this(new ItemStack(Material.WRITTEN_BOOK), title, author, pages);
        }

        BookImpl(@NotNull ItemStack itemStack, @NotNull FkComponent title, @NotNull FkComponent author, @NotNull FkComponent... pages) {
            this.itemStack = itemStack;
            this.title = title;
            this.author = author;
            this.pages = Arrays.asList(pages);
            this.assignMeta();
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
            final List<String> jsonPages = this.pages.stream().map(BungeeImpl::toJson).collect(Collectors.toList());
            return fixChangePage(new Gson().toJson(jsonPages));
        }

        @Override
        public @NotNull ItemStack asItemStack() {
            return this.itemStack;
        }

        @Override
        public void open(@NotNull Player player) {
            if (BookViewer.INSTANCE == null) {
                player.openBook(this.itemStack);
            } else {
                BookViewer.INSTANCE.openBook(player, this);
            }
        }

        @Override
        public @NotNull String nbt() {
            if (this.nbt == null) {
                this.nbt = FkBook.super.nbt();
            }
            return this.nbt;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BookImpl)) return false;
            BookImpl book = (BookImpl) o;
            return this.title.equals(book.title) &&
                    this.author.equals(book.author) &&
                    this.pages.equals(book.pages);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.title, this.author, this.pages);
        }

        @Override
        public String toString() {
            return "BukkitBook{" +
                    "title=" + this.title +
                    ", author=" + this.author +
                    '}';
        }

        @SuppressWarnings("deprecation")
        private void assignMeta() {
            final BookMeta meta = (BookMeta) this.itemStack.getItemMeta();
            meta.setTitle(this.title.toLegacyText());
            meta.setAuthor(this.author.toLegacyText());
            if (Environment.hasSpigotBookPages()) {
                meta.spigot().setPages(BungeeImpl.asBungee(this.pages.stream()));
            }
            this.itemStack.setItemMeta(meta);
        }

        private static @NotNull String fixChangePage(@NotNull String jsonRepresentation) {
            if (CHANGE_PAGE_FIX == null) return jsonRepresentation;
            return CHANGE_PAGE_FIX.matcher(jsonRepresentation).replaceAll("\\\\\"action\\\\\":\\\\\"change_page\\\\\",\\\\\"value\\\\\":$1");
        }
    }
}
