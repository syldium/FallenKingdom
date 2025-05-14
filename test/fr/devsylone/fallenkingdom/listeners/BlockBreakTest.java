package fr.devsylone.fallenkingdom.listeners;

import fr.devsylone.fallenkingdom.FkMock;
import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.ChestsRoom;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockBreakTest {

    private PlayerMock player;
    private Base playerBase;
    private Location playerBaseLocation;
    private Location enemyBaseLocation;
    private Block chest;

    @BeforeEach
    public void setUp() {
        this.player = MockUtils.getDefaultPlayer();
        this.playerBaseLocation = new Location(this.player.getWorld(), 700, 70, -500);
        this.enemyBaseLocation = new Location(this.player.getWorld(), 400, 70, 0);
        final FkMock plugin = MockUtils.getPluginMockSafe();
        final FkPI fkPI = plugin.getFkPI();
        final Team playerTeam = fkPI.getTeamManager().createTeam("break-test");
        this.playerBase = new Base(playerTeam, this.playerBaseLocation, 5, null, (byte) 0);
        playerTeam.setBase(this.playerBase);
        playerTeam.addPlayer(this.player.getName());
        final Team enemyTeam = fkPI.getTeamManager().createTeam("break-enemy");
        enemyTeam.setBase(new Base(enemyTeam, this.enemyBaseLocation, 5, null, (byte) 0));

        this.chest = this.playerBaseLocation.add(0, 1, 0).getBlock();
        this.chest.setType(Material.CHEST);
        ((ChestsRoom) this.playerBase.getNexus()).newChest(this.chest.getLocation());

        plugin.getWorldManager().invalidateBaseWorldsCache(fkPI.getTeamManager());
        plugin.getGame().setState(Game.GameState.STARTED);
    }

    @AfterEach
    public void tearDown() {
        final FkMock plugin = MockUtils.getPluginMockSafe();
        final FkPI fkPI = plugin.getFkPI();
        fkPI.getTeamManager().removeTeam("break-test");
        fkPI.getTeamManager().removeTeam("break-enemy");
        plugin.getGame().setState(Game.GameState.BEFORE_STARTING);
    }

    @Test
    public void breakInBase() {
        assertTrue(this.player.breakBlock(this.playerBaseLocation.getBlock()), "Player should be able to break block in their base");
    }

    @Test
    public void breakWildness() {
        final Location location = new Location(this.player.getWorld(), 0, 70, 0);
        assertTrue(this.player.breakBlock(location.getBlock()), "Player should be able to break block in wilderness");
    }

    @Test
    public void breakInEnemyBase() {
        assertFalse(this.player.breakBlock(this.enemyBaseLocation.getBlock()), "Player should not be able to break block in enemy base");
    }

    @Test
    public void breakRemoveChest() {
        assertTrue(this.player.breakBlock(this.chest), "Player should be able to break a chest in their base");
        assertFalse(this.playerBase.getNexus().isDefined(), "Chest should be removed from the base");
    }

    @Test
    public void operatorBreakChest() {
        final Player operator = MockUtils.getServerMockSafe().addPlayer();
        operator.setOp(true);
        operator.setGameMode(org.bukkit.GameMode.CREATIVE);
        assertTrue(operator.breakBlock(this.chest), "Operators should be able to break a chest in any base");
        assertFalse(this.playerBase.getNexus().isDefined(), "Chest should be removed from the base");
    }
}
