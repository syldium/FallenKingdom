package fr.devsylone.fallenkingdom;

import fr.devsylone.fallenkingdom.commands.FkCommandExecutor;
import fr.devsylone.fallenkingdom.display.GlobalDisplayService;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.manager.LanguageManager;
import fr.devsylone.fallenkingdom.manager.ListenersManager;
import fr.devsylone.fallenkingdom.manager.PlayerManagerMock;
import fr.devsylone.fallenkingdom.manager.TipsManager;
import fr.devsylone.fallenkingdom.manager.WorldManager;
import fr.devsylone.fallenkingdom.manager.saveable.DeepPauseManager;
import fr.devsylone.fallenkingdom.manager.saveable.PortalsManager;
import fr.devsylone.fallenkingdom.manager.saveable.ScoreboardManager;
import fr.devsylone.fallenkingdom.manager.saveable.StarterInventoryManager;
import fr.devsylone.fallenkingdom.pause.PauseRestorer;
import fr.devsylone.fkpi.FkPI;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Arrays;
import java.util.Objects;

import static org.mockito.Mockito.mock;

public class FkMock extends Fk {

    public FkMock() {
        instance = this;
    }

    @Override
    public void onEnable() {
        ListenersManager.registerListeners(this);
        getConfig().set("lang", "fr");
        getConfig().set("world-check", true);
        getConfig().set("affected-worlds", Arrays.asList("normal", "nether"));
        languageManager = new LanguageManager();
        languageManager.init(this);

        worldManager = new WorldManager(this);
        fkPI = new FkPI();

        commandManager = new FkCommandExecutor(this, Objects.requireNonNull(getCommand("fk"), "Unable to register /fk command"));

        displayService = new GlobalDisplayService();
        playerManager = new PlayerManagerMock(displayService);
        pauseRestorer = mock(PauseRestorer.class);
        starterInventoryManager = new StarterInventoryManager();
        scoreboardManager = new ScoreboardManager();
        deepPauseManager = mock(DeepPauseManager.class);
        tipsManager = new TipsManager();
        tipsManager.startBroadcasts();
        portalsManager = new PortalsManager();

        game = new Game();

        fkPI.getTeamManager().createTeam("blue");
    }

    @Override
    public void reset() {
        Bukkit.getScheduler().cancelTasks(this);
        game = new Game();
        for (World world : Bukkit.getWorlds()) {
            world.setFullTime(2000L);
        }
    }
}
