package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.display.content.Content;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fallenkingdom.version.tracker.ChatMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.function.BiConsumer;

public class ActionBarDisplayService extends SimpleDisplayService {

    /**
     * Envoie le message dans la barre d'action.
     */
    public static final BiConsumer<@NotNull Player, @NotNull String> SEND_ACTION_BAR;

    static {
        BiConsumer<Player, String> sendActionBar;
        try {
            Player.class.getMethod("sendActionBar", String.class);
            sendActionBar = Player::sendActionBar;
        } catch (NoSuchMethodException ignored) {
            try {
                Player.Spigot.class.getMethod("sendMessage", ChatMessageType.class, BaseComponent[].class);
                sendActionBar = (player, message) -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            } catch (NoSuchMethodException nms) {
                sendActionBar = new NMSActionBar();
            }
        }
        SEND_ACTION_BAR = sendActionBar;
    }

    public ActionBarDisplayService(@NotNull Content content) {
        super(DisplayType.ACTIONBAR, content);
    }

    @Override
    public void show(@NotNull Player player, @NotNull String message) {
        SEND_ACTION_BAR.accept(player, message);
    }

    @Override
    public void hide(@NotNull Player player, @NotNull FkPlayer fkPlayer) {

    }

    @Override
    public @NotNull ActionBarDisplayService withValue(@NotNull Content next) {
        return new ActionBarDisplayService(next);
    }

    // 1.8 :cry:
    private static class NMSActionBar implements BiConsumer<Player, String> {

        private static final Constructor<?> PACKET_CONSTRUCTOR;

        static {
            try {
                final Class<?> packet = NMSUtils.nmsClass("network.protocol.game", "PacketPlayOutChat");
                PACKET_CONSTRUCTOR = packet.getConstructor(ChatMessage.CHAT_BASE_COMPONENT, byte.class);
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                throw new ExceptionInInitializerError(ex);
            }
        }

        @Override
        public void accept(Player player, String message) {
            try {
                PacketUtils.sendPacket(player, PACKET_CONSTRUCTOR.newInstance(ChatMessage.legacyTextComponent(message), (byte) 2));
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
                player.sendMessage(message);
            }
        }
    }
}
