package fr.devsylone.fallenkingdom.commands.teams;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.commands.CommandTest;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SetBaseCommandTest extends CommandTest {

    @Test
    public void setBase() {
        assertNull(MockUtils.getBlueTeam().getBase());
        // Charge les chunks autour du joueur
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                MockUtils.getConstantPlayer().getWorld().getChunkAt(x, y);
            }
        }
        assertRun(MockUtils.getConstantPlayer(), "team setBase blue 5 cobblestone");
        assertNotNull(MockUtils.getBlueTeam().getBase());
        assertEquals(5, MockUtils.getBlueTeam().getBase().getRadius());
        assertEquals(MockUtils.getBlueTeam(), MockUtils.getBlueTeam().getBase().getTeam());
        assertEquals(MockUtils.getConstantPlayer().getLocation(), MockUtils.getBlueTeam().getBase().getCenter());
    }

    @Test
    public void setBase_Invalid() {
        assertRun(MockUtils.getConstantPlayer(), "team setBase oOoOO 10", CommandResult.STATE_ERROR);
        assertRun(MockUtils.getConstantPlayer(), "team setBase " + MockUtils.getBlueTeam().getName() + " 1", CommandResult.INVALID_ARGS);
    }
}
