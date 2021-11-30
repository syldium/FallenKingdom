package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.MockUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GameHelper {

    public static void setDay(int day) {
        MockUtils.getPluginMockSafe().getGame().day = day;
    }

    public static void assertGameStarted() {
        Game game = MockUtils.getPluginMockSafe().getGame();
        if (game.state.equals(Game.GameState.BEFORE_STARTING) || game.state.equals(Game.GameState.PAUSE)) {
            fail("Game state shouldn't be BEFORE_STARTING or PAUSE.");
        }
    }

    public static void assertGameRunnableStarted() {
        Game game = MockUtils.getPluginMockSafe().getGame();
        assertThat("Game runnable should be known.", game.task, instanceOf(GameRunnable.class));
        assertTrue(MockUtils.getServerMockSafe().getScheduler().isQueued(game.task.getTaskId()), "Game runnable should be scheduled.");
        assertEquals(Game.GameState.STARTED, game.state, "Game state should be started.");
    }

    public static void assertGameRunnableStopped() {
        Game game = MockUtils.getPluginMockSafe().getGame();
        assertNull(game.task, "Game shouldn't have a known runnable.");
        assertNotEquals(Game.GameState.STARTED, game.state, "Game state shouldn't be started.");
    }
}
