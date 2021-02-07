package fr.devsylone.fallenkingdom.util;

import org.bukkit.NamespacedKey;
import org.junit.jupiter.api.Test;

import static fr.devsylone.fallenkingdom.utils.KeyHelper.parseKey;
import static org.bukkit.NamespacedKey.MINECRAFT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyHelperTest {

    @Test
    public void minecraftIsDefaultNamespace() {
        NamespacedKey key = parseKey("fallenkingdom");
        assertEquals(MINECRAFT, key.getNamespace());
        assertEquals("fallenkingdom", key.getKey());
    }

    @Test
    public void customNamespace() {
        NamespacedKey key = parseKey("fk:demo");
        assertEquals("fk", key.getNamespace());
        assertEquals("demo", key.getKey());
    }
}
