package fr.devsylone.fallenkingdom.version.title;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fallenkingdom.version.tracker.ChatMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

class NMSTitleSender implements TitleSender {

    private static final Constructor<?> PACKET_TITLE_TIMES;
    private static final Constructor<?> PACKET_TITLE_TEXT;
    private static final Class<?> TITLE_ACTION;

    static {
        try {
            final String packetsPackage = "network.protocol.game";
            final Class<?> packetTitleClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutTitle");
            TITLE_ACTION = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutTitle$EnumTitleAction");
            PACKET_TITLE_TIMES = packetTitleClass.getConstructor(int.class, int.class, int.class);
            PACKET_TITLE_TEXT = packetTitleClass.getConstructor(TITLE_ACTION, ChatMessage.CHAT_BASE_COMPONENT);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public void sendTitle(@NotNull Player player, @NotNull String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            sendTimesPacket(player, fadeIn, stay, fadeOut);
            if (subtitle != null) {
                sendTitlePacket(player, TitlePart.SUBTITLE, subtitle);
            }
            sendTitlePacket(player, TitlePart.TITLE, title);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    private void sendTimesPacket(@NotNull Player player, int fadeIn, int stay, int fadeOut) throws ReflectiveOperationException {
        final Object packet = PACKET_TITLE_TIMES.newInstance(fadeIn, stay, fadeOut);
        PacketUtils.sendPacket(player, packet);
    }

    private void sendTitlePacket(@NotNull Player player, @NotNull TitlePart part, @NotNull String content) throws ReflectiveOperationException {
        final Object packet = PACKET_TITLE_TEXT.newInstance(
                TITLE_ACTION.getDeclaredField(part.name()).get(null),
                ChatMessage.legacyTextComponent(content)
        );
        PacketUtils.sendPacket(player, packet);
    }

    private enum TitlePart {
        TITLE,
        SUBTITLE
    }
}
