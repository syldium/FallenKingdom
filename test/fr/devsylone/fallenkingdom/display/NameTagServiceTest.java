package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fkpi.managers.TeamManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NameTagServiceTest {

    private final Scoreboard scoreboard = MockUtils.getServerMockSafe().getScoreboardManager().getMainScoreboard();
    private final TeamManager teamManager = MockUtils.getPluginMockSafe().getFkPI().getTeamManager();

    @Test
    public void reuseVanillaTeam() {
        final String playerName = "noop";
        final String teamName = "live1";
        final Team scoreboardTeam = this.scoreboard.registerNewTeam(teamName);
        scoreboardTeam.setCanSeeFriendlyInvisibles(false);

        // Try to create a team with the same name
        this.teamManager.createTeam(teamName);
        this.teamManager.addPlayer(playerName, teamName);
        assertEquals(Collections.singleton(playerName), scoreboardTeam.getEntries());

        this.teamManager.removeTeam(teamName);
        assertNull(scoreboardTeam.getScoreboard(), "The scoreboard team should be unregistered.");
    }

    @Test
    public void removeInUnaffectedWorlds() {
        final Player player = MockUtils.getDefaultPlayer();
        final String teamName = "live2";
        fr.devsylone.fkpi.teams.Team fkTeam = this.teamManager.createTeam(teamName);
        this.teamManager.addPlayer(player.getName(), teamName);

        final Team scoreboardTeam = this.scoreboard.getTeam(teamName);
        assertNotNull(scoreboardTeam);
        assertEquals(Collections.singletonList(player.getName()), fkTeam.getPlayers());
        assertEquals(Collections.singleton(player.getName()), scoreboardTeam.getEntries());

        World currentWorld = player.getWorld();
        player.teleport(new Location(MockUtils.getUnaffectedWorld(), 0, 80, 0));
        new PlayerChangedWorldEvent(player, currentWorld).callEvent(); // https://github.com/MockBukkit/MockBukkit/pull/519
        assertEquals(Collections.singletonList(player.getName()), fkTeam.getPlayers());
        assertEquals(Collections.emptySet(), scoreboardTeam.getEntries());
    }
}
