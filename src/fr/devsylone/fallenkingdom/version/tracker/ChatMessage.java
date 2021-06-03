package fr.devsylone.fallenkingdom.version.tracker;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ChatMessage {

    public static final Class<?> CRAFT_CHAT_MESSAGE;
    private static final Method MESSAGE_FROM_STRING;

    private ChatMessage() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    static {
        try {
            CRAFT_CHAT_MESSAGE = NMSUtils.obcClass("util.CraftChatMessage");
            MESSAGE_FROM_STRING = CRAFT_CHAT_MESSAGE.getMethod("fromString", String.class);
        } catch (ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Convertit un message legacy en un IChatBaseComponent.
     *
     * @param message Le message à convertir
     * @return Le composant de chat NMS
     */
    public static @NotNull Object fromString(String message) {
        try {
            return MESSAGE_FROM_STRING.invoke(null, message);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
