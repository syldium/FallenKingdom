package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.MockUtils;
import org.junit.Assert;

import static org.hamcrest.Matchers.instanceOf;

public class GameHelper {

    public static void setDay(int day) {
        MockUtils.getPluginMockSafe().getGame().day = day;
    }

    public static void assertGameStarted() {
        Game game = MockUtils.getPluginMockSafe().getGame();
        if (game.state.equals(Game.GameState.BEFORE_STARTING) || game.state.equals(Game.GameState.PAUSE)) {
            Assert.fail("Game state shouldn't be BEFORE_STARTING or PAUSE.");
        }
    }

    public static void assertGameRunnableStarted() {
        Game game = MockUtils.getPluginMockSafe().getGame();
        Assert.assertThat("Game runnable should be known.", game.task, instanceOf(GameRunnable.class));
        Assert.assertTrue("Game runnable should be scheduled.", MockUtils.getServerMockSafe().getScheduler().isQueued(game.task.getTaskId()));
        Assert.assertEquals("Game state should be started.", Game.GameState.STARTED, game.state);
    }

    public static void assertGameRunnableStopped() {
        Game game = MockUtils.getPluginMockSafe().getGame();
        Assert.assertNull("Game shouldn't have a known runnable.", game.task);
        Assert.assertNotEquals("Game state shouldn't be started.", Game.GameState.STARTED, game.state);
    }
}
