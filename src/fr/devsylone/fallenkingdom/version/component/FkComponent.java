package fr.devsylone.fallenkingdom.version.component;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface FkComponent {

    static @NotNull FkComponent space() {
        return Components.space();
    }

    static @NotNull FkComponent join(FkComponent... children) {
        return Components.join(children);
    }

    static @NotNull FkComponent text(String content, ChatColor... colors) {
        return Components.text(content, colors);
    }

    default @NotNull FkComponent changePage(int page) {
        if (Components.isChangePageSupported()) {
            return this.interact(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, String.valueOf(page)));
        } else {
            return this.interact(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/change_page " + page));
        }
    }

    default @NotNull FkComponent command(@NotNull String command) {
        return this.interact(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
    }

    @Contract("_ -> this")
    @NotNull FkComponent interact(@NotNull ClickEvent event);

    @Contract("_ -> this")
    default @NotNull FkComponent hover(@NotNull String content) {
        return this.hover(FkComponent.text(content));
    }

    @Contract("_ -> this")
    @NotNull FkComponent hover(@NotNull FkComponent component);
}
