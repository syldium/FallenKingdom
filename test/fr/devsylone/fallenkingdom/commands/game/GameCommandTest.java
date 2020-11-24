package fr.devsylone.fallenkingdom.commands.game;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.commands.CommandTest;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.devsylone.fallenkingdom.game.GameHelper.assertGameRunnableStarted;
import static fr.devsylone.fallenkingdom.game.GameHelper.assertGameRunnableStopped;
import static fr.devsylone.fallenkingdom.game.GameHelper.assertGameStarted;

public class GameCommandTest extends CommandTest {

    @BeforeEach
    public void reset() {
        MockUtils.getPluginMockSafe().reset();
        assertGameRunnableStopped();
    }

    @Test
    public void gameStart_Normal() {
        assertRun("game start");
        MockUtils.getServerMockSafe().getScheduler().performOneTick();
        assertGameStarted();
        assertRun("game stop");
        assertGameRunnableStopped();
    }

    @Test
    public void gameStart_WithPause() {
        assertRun("game start");
        MockUtils.getServerMockSafe().getScheduler().performTicks(4000L);
        assertGameRunnableStarted();
        assertRun("game pause");
        assertGameRunnableStopped();
        MockUtils.getServerMockSafe().getScheduler().performOneTick();
        assertRun("game resume");
        MockUtils.getServerMockSafe().getScheduler().performOneTick();
        assertGameRunnableStarted();
    }

    @Test
    public void gamePause() {
        assertRun("game start");
        MockUtils.getServerMockSafe().getScheduler().performOneTick();
        assertGameStarted();
        assertRun("game pause");
        assertRun("game pause", CommandResult.STATE_ERROR);
        assertGameRunnableStopped();
        MockUtils.getServerMockSafe().getScheduler().performOneTick();
        assertRun("game stop");
        assertRun("game pause", CommandResult.STATE_ERROR);
        assertRun("game stop", CommandResult.STATE_ERROR);
        assertGameRunnableStopped();
    }

    @Test
    public void gameStopStart() {
        assertRun("game start");
        assertGameStarted();
        assertRun("game stop");
        assertGameRunnableStopped();
        assertRun("game start");
        assertGameStarted();
    }
}
