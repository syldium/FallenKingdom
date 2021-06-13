package fr.devsylone.fallenkingdom.version.component;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FkBook {

    static @NotNull FkBook book(FkComponent title, FkComponent author, FkComponent... pages) {
        return Components.ADVENTURE ? new AdventureImpl.BookImpl(title, author, pages) : new BukkitImpl.BookImpl(title, author, pages);
    }

    @NotNull FkComponent title();

    @NotNull FkComponent author();

    @NotNull List<FkComponent> pages();

    @NotNull String jsonPages();

    @NotNull ItemStack asItemStack();

    void open(@NotNull Player player);

    default @NotNull String nbt() {
        final String legacyTitle = this.title().toLegacyText();
        final String legacyAuthor = this.author().toLegacyText();
        return "{pages:" + this.jsonPages() + ",title:\"" + legacyTitle + "\",author:\"" + legacyAuthor + "\"}";
    }
}
