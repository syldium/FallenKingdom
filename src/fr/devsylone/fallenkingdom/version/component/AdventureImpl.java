package fr.devsylone.fallenkingdom.version.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import static fr.devsylone.fallenkingdom.version.component.Components.TEXT_DECORATIONS;

class AdventureImpl implements FkComponent {

    static final AdventureImpl SPACE = new AdventureImpl(Component.space());

    Component component;

    AdventureImpl(@NotNull Component component) {
        this.component = component;
    }

    AdventureImpl(String content, ChatColor... colors) {
        this(Component.text(content, asAdventure(colors)));
    }

    AdventureImpl(FkComponent... components) {
        this(Component.join(Component.empty(), Arrays.stream(components).map(component -> ((AdventureImpl) component).component).collect(Collectors.toList())));
    }

    @Override
    public @NotNull FkComponent command(@NotNull String command) {
        this.component = this.component.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(command));
        return this;
    }

    @Override
    public @NotNull FkComponent interact(@NotNull ClickEvent event) {
        this.component = this.component.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(asAdventure(event.getAction()), event.getValue()));
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

    private static @NotNull Action asAdventure(ClickEvent.Action eventAction) {
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
}
