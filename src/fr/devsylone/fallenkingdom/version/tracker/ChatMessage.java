package fr.devsylone.fallenkingdom.version.tracker;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ChatMessage {

    public static final Class<?> CRAFT_CHAT_MESSAGE;
    public static final Class<?> CHAT_BASE_COMPONENT;
    private static final Method MESSAGE_FROM_STRING;
    private static Method MESSAGE_FROM_JSON;

    private ChatMessage() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    static {
        try {
            CRAFT_CHAT_MESSAGE = NMSUtils.obcClass("util.CraftChatMessage");
            MESSAGE_FROM_STRING = CRAFT_CHAT_MESSAGE.getMethod("fromString", String.class);
            CHAT_BASE_COMPONENT = NMSUtils.nmsClass("network.chat", "IChatBaseComponent", "Component");
        } catch (ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }

        try {
            final Class<?> serializer = CHAT_BASE_COMPONENT.getDeclaredClasses()[0];
            MESSAGE_FROM_JSON = NMSUtils.getMethod(serializer, CHAT_BASE_COMPONENT, String.class);
        } catch (ReflectiveOperationException ignored) {}
    }

    /**
     * Convertit un message legacy en un IChatBaseComponent.
     *
     * @param message Le message à convertir
     * @return Le composant de chat NMS
     */
    public static @NotNull Object fromString(String message) {
        try {
            return Array.get(MESSAGE_FROM_STRING.invoke(null, message), 0);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enveloppe un message legacy d'un composant texte.
     * <p>
     * Cela permet de gérer la couleur pour certaines (très) vieilles versions
     * et ne devrait pas être utilisé sur les serveurs à jour.
     *
     * @param message Le message à envelopper
     * @return Le composant de chat NMS
     */
    public static @NotNull Object legacyTextComponent(String message) {
        if (MESSAGE_FROM_JSON == null) {
            return fromString(message);
        }

        final StringBuilder builder = new StringBuilder("{\"text\":\"");
        for (char c : message.toCharArray()) {
            if (c == '"') {
                builder.append('\\');
            }
            builder.append(c);
        }
        builder.append("\"}");
        return legacyTextComponentString(builder.toString());
    }

    /**
     * Convertit un message legacy en un composant texte.
     * <p>
     * Cela permet de gérer la couleur pour certaines (très) vieilles versions
     * et ne devrait pas être utilisé sur les serveurs à jour.
     *
     * @param message Le message à convertir
     * @return Le composant de chat NMS
     */
    public static @NotNull Object legacyTextComponentString(String message) {
        if (MESSAGE_FROM_JSON == null) {
            return fromString(message);
        }
        try {
            return MESSAGE_FROM_JSON.invoke(null, message);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
