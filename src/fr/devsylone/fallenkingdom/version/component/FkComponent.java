package fr.devsylone.fallenkingdom.version.component;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface FkComponent {

    static @NotNull FkComponent newline() {
        return Components.newline();
    }

    static @NotNull FkComponent space() {
        return Components.space();
    }

    static @NotNull FkComponent join(FkComponent... children) {
        return Components.join(children);
    }

    static @NotNull FkComponent text(String content, ChatColor... colors) {
        return Components.text(content, colors);
    }

    @Contract("_ -> this")
    @NotNull FkComponent changePage(int page);

    @Contract("_ -> this")
    @NotNull FkComponent command(@NotNull String command);

    @Contract("_ -> this")
    @NotNull FkComponent openUrl(@NotNull String url);

    @Contract("_ -> this")
    default @NotNull FkComponent hover(@NotNull String content) {
        return this.hover(FkComponent.text(content));
    }

    @Contract("_ -> this")
    @NotNull FkComponent hover(@NotNull FkComponent component);

    @NotNull String toLegacyText();
}
