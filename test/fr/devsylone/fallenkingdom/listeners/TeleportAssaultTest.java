package fr.devsylone.fallenkingdom.listeners;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.PortalCreateEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.block.BlockMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TeleportAssaultTest {

    private PlayerMock player;
    private Team playerTeam;
    private Team enemyTeam;

    @BeforeEach
    public void setUp() {
        this.player = MockUtils.getDefaultPlayer();
        Fk plugin = MockUtils.getPluginMockSafe();
        FkPI fkPI = plugin.getFkPI();
        this.playerTeam = fkPI.getTeamManager().createTeam("portal-test");
        this.playerTeam.addPlayer(this.player.getName());
        final Location playerBaseLocation = new Location(this.player.getWorld(), -500, 70, -500);
        final Base playerBase = new Base(this.playerTeam, playerBaseLocation, 20, Material.COBBLESTONE, (byte) 0);
        this.playerTeam.setBase(playerBase);

        this.enemyTeam = fkPI.getTeamManager().createTeam("portal-enemy");
        final Location enemyBaseLocation = new Location(this.player.getWorld(), 500, 70, 500);
        final Base enemyBase = new Base(this.enemyTeam, enemyBaseLocation, 20, Material.COBBLESTONE, (byte) 0);
        this.enemyTeam.setBase(enemyBase);
        plugin.getWorldManager().invalidateBaseWorldsCache(fkPI.getTeamManager());
        fkPI.getRulesManager().setRule(Rule.NETHER_ASSAULT, false);
        fkPI.getRulesManager().setRule(Rule.ENDERPEARL_ASSAULT, false);
    }

    @AfterEach
    public void tearDown() {
        this.playerTeam.removePlayer(this.player.getName());
        this.enemyTeam.removePlayer(this.player.getName());
        FkPI.getInstance().getTeamManager().removeTeam(this.playerTeam.getName());
        FkPI.getInstance().getTeamManager().removeTeam(this.enemyTeam.getName());
    }

    @Test
    public void linkPortalInEnemyBase() {
        assertFalse(createPortalEvent(), "The portal should not be created in an enemy base");
    }

    @Test
    public void linkPortalInSelfBase() {
        this.playerTeam.removePlayer(this.player.getName());
        this.enemyTeam.addPlayer(this.player.getName());
        assertTrue(createPortalEvent(), "The portal should be created in the base");
    }

    @Test
    public void teleportInEnemyBase() {
        assertFalse(createTeleportEvent(TeleportCause.NETHER_PORTAL), "The player should not be teleported in an enemy base");
        assertFalse(createTeleportEvent(TeleportCause.ENDER_PEARL), "The player should not be teleported in an enemy base");
        assertFalse(createTeleportEvent(TeleportCause.CHORUS_FRUIT), "The player should not be teleported in an enemy base");
    }

    @Test
    public void teleportInSelfBase() {
        this.playerTeam.removePlayer(this.player.getName());
        this.enemyTeam.addPlayer(this.player.getName());
        assertTrue(createTeleportEvent(TeleportCause.NETHER_PORTAL), "The player should be teleported in the base");
        assertTrue(createTeleportEvent(TeleportCause.ENDER_PEARL), "The player should be teleported in the base");
        assertTrue(createTeleportEvent(TeleportCause.CHORUS_FRUIT), "The player should be teleported in the base");
    }

    @Test
    public void linkPublicPortal() {
        final Location portalLocation = new Location(this.player.getWorld(), 0, 70, 0);
        assertTrue(createPortalEvent(portalLocation), "The portal should be created");
    }

    @Test
    public void teleportNeutralLocation() {
        final Location location = new Location(this.player.getWorld(), 0, 70, 0);
        assertTrue(createTeleportEvent(TeleportCause.ENDER_PEARL, location), "The player should be teleported");
        assertTrue(createTeleportEvent(TeleportCause.CHORUS_FRUIT, location), "The player should be teleported");
        assertTrue(createTeleportEvent(TeleportCause.NETHER_PORTAL, location), "The player should be teleported");
    }

    @SuppressWarnings("UnstableApiUsage")
    private boolean createPortalEvent(Location to) {
        final BlockMock blockMock = new BlockMock(Material.AIR, to);
        return new PortalCreateEvent(Collections.singletonList(blockMock.getState()), this.player.getWorld(), this.player, PortalCreateEvent.CreateReason.NETHER_PAIR).callEvent();
    }

    private boolean createPortalEvent() {
        return createPortalEvent(this.enemyTeam.getBase().getCenter().add(15, 0, -3));
    }

    private boolean createTeleportEvent(TeleportCause cause, Location to) {
        return new PlayerTeleportEvent(this.player, this.player.getLocation(), to, cause).callEvent();
    }

    private boolean createTeleportEvent(TeleportCause cause) {
        return createTeleportEvent(cause, this.enemyTeam.getBase().getCenter().add(15, 0, -3));
    }
}
