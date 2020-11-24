package fr.devsylone.fallenkingdom.commands.teams;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.commands.CommandTest;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class SetBaseCommandTest extends CommandTest {

    @Test
    public void setBase() {
        Assert.assertNull(MockUtils.getBlueTeam().getBase());
        assertRun(MockUtils.getConstantPlayer(), "team setBase blue 5");
        Assert.assertNotNull(MockUtils.getBlueTeam().getBase());
        Assert.assertEquals(5, MockUtils.getBlueTeam().getBase().getRadius());
        Assert.assertEquals(MockUtils.getBlueTeam(), MockUtils.getBlueTeam().getBase().getTeam());
        Assert.assertEquals(MockUtils.getConstantPlayer().getLocation(), MockUtils.getBlueTeam().getBase().getCenter());
    }

    @Test
    public void setBase_Invalid() {
        assertRun(MockUtils.getConstantPlayer(), "team setBase oOoOO 10", CommandResult.STATE_ERROR);
        assertRun(MockUtils.getConstantPlayer(), "team setBase " + MockUtils.getBlueTeam().getName() + " 1", CommandResult.INVALID_ARGS);
    }
}
