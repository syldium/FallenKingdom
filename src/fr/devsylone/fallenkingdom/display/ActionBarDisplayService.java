package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class ActionBarDisplayService extends SimpleDisplayService {

    public static final BiConsumer<Player, String> SEND_ACTION_BAR;

    static {
        BiConsumer<Player, String> sendActionBar;
        try {
            Player.class.getMethod("sendActionBar", String.class);
            sendActionBar = Player::sendActionBar;
        } catch (NoSuchMethodException ex) {
            sendActionBar = (player, message) -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
        SEND_ACTION_BAR = sendActionBar;
    }

    public ActionBarDisplayService(@NotNull String value) {
        super(DisplayType.ACTIONBAR, value);
    }

    @Override
    public void show(@NotNull Player player, @NotNull String message) {
        SEND_ACTION_BAR.accept(player, message);
    }

    @Override
    public void hide(@NotNull Player player, @NotNull FkPlayer fkPlayer) {

    }

    @Override
    public @NotNull ActionBarDisplayService withValue(@NotNull String next) {
        return new ActionBarDisplayService(next);
    }
}
