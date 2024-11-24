package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.display.content.Content;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fallenkingdom.version.tracker.ChatMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
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

    private final boolean doesResend;

    public ActionBarDisplayService(@NotNull ConfigurationSection section) {
        super(DisplayType.ACTIONBAR, Content.fromConfig(section.get(CONTENT)));
        this.doesResend = section.getBoolean(DOES_RESEND, true);
    }

    public ActionBarDisplayService(@NotNull Content content) {
        this(content, true);
    }

    public ActionBarDisplayService(@NotNull Content content, boolean doesResend) {
        super(DisplayType.ACTIONBAR, content);
        this.doesResend = doesResend;
    }

    @Override
    public boolean contains(@NotNull PlaceHolder placeHolder) {
        return (this.doesResend && placeHolder == PlaceHolder.MINUTE) || super.contains(placeHolder);
    }

    @Override
    public boolean containsAny(@NotNull PlaceHolder... placeHolders) {
        if (this.doesResend) {
            for (PlaceHolder placeHolder : placeHolders) {
                if (placeHolder == PlaceHolder.MINUTE) {
                    return true;
                }
            }
        }
        return super.containsAny(placeHolders);
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
        return new ActionBarDisplayService(next, this.doesResend);
    }

    private static final String DOES_RESEND = "does-resend";
    private static final String CONTENT = "content";

    @Override
    void save(@NotNull ConfigurationSection section) {
        section.set(DOES_RESEND, this.doesResend);
        this.content().save(section, CONTENT);
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
