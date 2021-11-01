package fr.devsylone.fallenkingdom.updater;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PluginVersionTest {

    @Test
    public void versionEquals() {
        assertEquals(new PluginVersion(1, 16), new PluginVersion(1, 16, 0));
        assertNotEquals(new PluginVersion(1, 15), new PluginVersion(1, 15, 2));
        assertEquals(new PluginVersion("2.0-SNAPSHOT"), new PluginVersion("2.0-SNAPSHOT"));
        assertNotEquals(new PluginVersion("2.0-SNAPSHOT"), new PluginVersion(2));
        assertEquals(new PluginVersion("v1.3"), new PluginVersion(1, 3));
    }

    @Test
    public void versionCompare() {
        assertEquals(1, new PluginVersion(1, 14, 4).compareTo(new PluginVersion(1, 14, 2)));
        assertEquals(0, new PluginVersion(1, 15).compareTo(new PluginVersion(1, 15)));
        assertEquals(-1, new PluginVersion("1.0-SNAPSHOT").compareTo(new PluginVersion("1.0")));
    }
}
