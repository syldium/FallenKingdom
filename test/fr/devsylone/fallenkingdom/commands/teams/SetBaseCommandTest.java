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

    @Disabled // Unimplemented
    @Test
    public void setBase() {
        assertNull(MockUtils.getBlueTeam().getBase());
        assertRun(MockUtils.getConstantPlayer(), "team setBase blue 5");
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
