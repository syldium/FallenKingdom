package fr.devsylone.fallenkingdom.listeners;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.managers.TeamManager;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockbukkit.mockbukkit.matcher.entity.EntityTeleportationMatcher.hasNotTeleported;
import static org.mockbukkit.mockbukkit.matcher.entity.EntityTeleportationMatcher.hasTeleported;

public class TntJumpTest {

    private WorldMock world;
    private Location[] tntLocs;
    private PlayerMock player;

    @BeforeEach
    public void setUp() {
        world = (WorldMock) MockUtils.getServerMockSafe().getWorlds().get(0);
        tntLocs = new Location[] {
                new Location(world, 21, 70, 20),
                new Location(world, 22, 70, 21)
        };
        for (Location location : tntLocs) {
            location.getBlock().setType(Material.TNT);
        }
        player = MockUtils.getDefaultPlayer();
        Fk plugin = MockUtils.getPluginMockSafe();
        TeamManager teamManager = plugin.getFkPI().getTeamManager();
        teamManager.getTeams().clear();

        Location aBaseCenter = new Location(world, 0, 70, 0);
        world.getBlockAt(aBaseCenter.getBlockX(), 67, aBaseCenter.getBlockZ()).setType(Material.GRASS_BLOCK);

        Team a = new Team("a");
        a.setBase(new Base(a, aBaseCenter, 20, Material.COBBLESTONE, (byte) 0));
        teamManager.getTeams().add(a);
        Team b = new Team("b");
        b.setBase(new Base(b, new Location(world, 100, 70, 100), 20, Material.COBBLESTONE, (byte) 0));
        b.addPlayer(player.getName());
        teamManager.getTeams().add(b);
        plugin.getWorldManager().invalidateBaseWorldsCache(plugin.getFkPI().getTeamManager());
    }

    @AfterEach
    public void tearDown() {
        for (Location location : tntLocs) {
            location.getBlock().setType(Material.AIR);
        }
        FkPI.getInstance().getTeamManager().getTeams().clear();
    }

    @Test
    public void tntJump_NotActive() {
        FkPI.getInstance().getRulesManager().setRule(Rule.TNT_JUMP, true);
        FkPI.getInstance().getTeamManager().removePlayerOfHisTeam(player.getName());
        FkPI.getInstance().getTeamManager().getTeam("b").addPlayer(player.getName());
        playMoveSequence();
        assertThat(player, hasNotTeleported());
    }

    @Test
    public void tntJump_Fired() {
        FkPI.getInstance().getRulesManager().setRule(Rule.TNT_JUMP, false);
        FkPI.getInstance().getTeamManager().removePlayerOfHisTeam(player.getName());
        FkPI.getInstance().getTeamManager().getTeam("b").addPlayer(player.getName());
        playMoveSequence();
        assertThat(player, hasTeleported(tntLocs[0].clone().add(0, 1, 0), 1.5));
    }

    @Test
    public void tntJump_NoFired() {
        FkPI.getInstance().getRulesManager().setRule(Rule.TNT_JUMP, false);
        FkPI.getInstance().getTeamManager().removePlayerOfHisTeam(player.getName());
        FkPI.getInstance().getTeamManager().getTeam("a").addPlayer(player.getName());
        playMoveSequence();
        assertThat(player, hasNotTeleported());
    }

    private void playMoveSequence() {
        fireMoveEvent(player, new Location(world, 22, 70, 22));
        fireMoveEvent(player, tntLocs[0].clone().add(0, 1, 0));
        fireMoveEvent(player, tntLocs[1].clone().add(0, 1, 0));
    }

    private void fireMoveEvent(PlayerMock player, Location to) {
        PlayerMoveEvent event = new PlayerMoveEvent(player, player.getLocation(), to);
        MockUtils.getServerMockSafe().getPluginManager().callEvent(event);
        player.setLocation(to);
    }
}
