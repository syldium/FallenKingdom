package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fkpi.teams.Base;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTest {

    private final Base base = new Base(null, new Location(null, 10, 70, 80), 10, Material.COBBLESTONE, (byte) 0);

    @Test
    public void isInside() {
        assertTrue(base.contains(null, 12, 75, 80));
        assertTrue(base.contains(null, 0, 70, 85));
        assertTrue(base.contains(null, 16, 90, 69, 1));
    }

    @Test
    public void isOutside() {
        assertFalse(base.contains(null, 2, 50, 91));
        assertFalse(base.contains(null, 16, 90, 68, 1));
    }
}
