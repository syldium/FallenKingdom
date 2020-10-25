package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.BooleanSupplier;

import static fr.devsylone.fallenkingdom.game.GameHelper.assertGameRunnableStarted;
import static fr.devsylone.fallenkingdom.game.GameHelper.assertGameRunnableStopped;

public class GameRunnableTest {

    private Game game;
    private GameRunnable runnable;

    @BeforeEach
    public void load() {
        game = MockUtils.getPluginMockSafe().getGame();
        MockUtils.getPluginMockSafe().stop();

        FkPI.getInstance().getRulesManager().setRule(Rule.NETHER_CAP, 1);
        FkPI.getInstance().getRulesManager().setRule(Rule.END_CAP, 2);
        FkPI.getInstance().getRulesManager().setRule(Rule.PVP_CAP, 3);
        FkPI.getInstance().getRulesManager().setRule(Rule.TNT_CAP, 4);
        FkPI.getInstance().getRulesManager().setRule(Rule.DAY_DURATION, 24000);

        game.setState(Game.GameState.STARTED);
        runnable = new GameRunnable(game);
        for (World world : Bukkit.getWorlds()) {
            world.setTime(23990L);
        }
        Assert.assertNull(game.task);
    }

    @Test
    public void capsActivation() {
        Assert.assertEquals(0, game.getDay());
        BooleanSupplier[] activations = new BooleanSupplier[] { game::isNetherEnabled, game::isEndEnabled, game::isPvpEnabled, game::isAssaultsEnabled };

        for (int day = 1; day < activations.length+1; day++) {
            runnable.incrementDay();
            Assert.assertEquals(day, game.getDay());
            for (int i = 0; i < activations.length; i++) {
                int activationDay = i + 1;
                BooleanSupplier supplier = activations[i];
                if (day >= activationDay) {
                    Assert.assertTrue("Cap should be active", supplier.getAsBoolean());
                } else {
                    Assert.assertFalse("Cap shouldn't be active", supplier.getAsBoolean());
                }
            }
        }
    }

    @Test
    public void newDay() {
        game.startTimer();
        Assert.assertEquals(0, game.getDay());
        MockUtils.getServerMockSafe().getScheduler().performTicks(24060L);
        for (World world : Bukkit.getWorlds()) {
            Assert.assertEquals(50L, world.getTime());
        }
        Assert.assertEquals(2, game.getDay());
    }

    @Test
    public void dayDurationImpact() {
        FkPI.getInstance().getRulesManager().setRule(Rule.DAY_DURATION, 1200);
        Assert.assertEquals(23990, game.getTime());
        game.startTimer();
        assertGameRunnableStarted();
        MockUtils.getServerMockSafe().getScheduler().performTicks(31L);
        Assert.assertEquals(30, game.getTime());
        for (World world : Bukkit.getWorlds()) {
            Assert.assertEquals(30L * 20L, world.getTime());
        }
    }

    @Test
    public void timerCreation() {
        game.startTimer();
        assertGameRunnableStarted();
    }

    @Test
    public void timerStop() {
        game.startTimer();
        assertGameRunnableStarted();
        for (int i = 0; i < 3; i++)
            game.stopTimer();
        game.setState(Game.GameState.PAUSE);
        assertGameRunnableStopped();
    }
}
