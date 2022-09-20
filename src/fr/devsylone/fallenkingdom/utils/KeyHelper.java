package fr.devsylone.fallenkingdom.utils;

import org.bukkit.NamespacedKey;

import static org.bukkit.NamespacedKey.MINECRAFT;

public final class KeyHelper {

    private KeyHelper() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static NamespacedKey parseKey(String string, int separatorIndex) {
        String namespace = separatorIndex >= 1 ? string.substring(0, separatorIndex) : MINECRAFT;
        String key = separatorIndex >= 0 ? string.substring(separatorIndex + 1) : string;
        return new NamespacedKey(namespace, key);
    }

    public static NamespacedKey parseKey(String string) {
        return parseKey(string, string.indexOf(':'));
    }

    public static NamespacedKey plugin(String value) {
        return new NamespacedKey("fallenkingdom", value);
    }
}
