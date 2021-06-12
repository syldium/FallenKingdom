package fr.devsylone.fallenkingdom.version.component;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

class BungeeImpl implements FkComponent {

    final BaseComponent component;

    BungeeImpl(BaseComponent component) {
        this.component = component;
    }

    BungeeImpl(String content, ChatColor... colors) {
        this(new TextComponent(content));
        for (ChatColor color : colors) {
            if (color == ChatColor.MAGIC) this.component.setObfuscated(true);
            else if (color == ChatColor.BOLD) this.component.setBold(true);
            else if (color == ChatColor.STRIKETHROUGH) this.component.setStrikethrough(true);
            else if (color == ChatColor.UNDERLINE) this.component.setUnderlined(true);
            else if (color == ChatColor.ITALIC) this.component.setItalic(true);
            else if (color != ChatColor.RESET) this.component.setColor(color);
        }
    }

    BungeeImpl(FkComponent... components) {
        this(new TextComponent(Arrays.stream(components).map(component -> ((BungeeImpl) component).component).toArray(BaseComponent[]::new)));
    }

    @Override
    public @NotNull FkComponent interact(@NotNull ClickEvent event) {
        this.component.setClickEvent(event);
        return this;
    }

    @Override
    public @NotNull FkComponent hover(@NotNull String content) {
        this.component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(content)));
        return this;
    }

    @Override
    public @NotNull FkComponent hover(@NotNull FkComponent component) {
        this.component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new BaseComponent[]{((BungeeImpl) component).component})));
        return this;
    }
}
