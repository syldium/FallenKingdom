package fr.devsylone.fallenkingdom.commands.teams;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.commands.CommandTest;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fkpi.managers.TeamManager;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.scoreboard.Scoreboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TeamCommandTest extends CommandTest {

    private Scoreboard scoreboard;
    private TeamManager teamManager;

    @BeforeEach
    public void unregisterTeams() {
        this.scoreboard = MockUtils.getServerMockSafe().getScoreboardManager().getMainScoreboard();
        this.teamManager = MockUtils.getPluginMockSafe().getFkPI().getTeamManager();
        for (Team team : new ArrayList<>(this.teamManager.getTeams())) {
            this.teamManager.removeTeam(team.getName());
        }
        this.scoreboard.getTeams().forEach(org.bukkit.scoreboard.Team::unregister);
    }

    @Test
    public void create() {
        assertRun("team create purple");
        Team purpleTeam = this.teamManager.getTeam("purple");
        org.bukkit.scoreboard.Team scoreboardTeam = this.scoreboard.getTeam("purple");
        assertNotNull(purpleTeam);
        assertNotNull(scoreboardTeam);
        assertEquals("purple", purpleTeam.getName());
        assertEquals(Color.VIOLET, purpleTeam.getColor());

        assertEquals(NamedTextColor.DARK_PURPLE, scoreboardTeam.color(), "The scoreboard team should have the same white color as the fk team.");
        assertEquals(purpleTeam.getName(), scoreboardTeam.getDisplayName(), "The scoreboard team should have the same name as the fk team.");
        assertEquals(Collections.emptySet(), scoreboardTeam.getEntries(), "The scoreboard team shouldn't have any entries.");

        assertRun("team create purple", CommandResult.STATE_ERROR);
        assertRun("team create violet");
    }

    @Test
    public void remove() {
        this.teamManager.createTeam("DemoR");
        this.teamManager.getTeam("DemoR").addPlayer(MockUtils.getConstantPlayer().getName());
        assertRun("team remove DemoR");
        assertNull(this.scoreboard.getTeam("DemoR"), "The scoreboard team should no longer exists.");
        assertRun("team remove unknown", CommandResult.STATE_ERROR);
        assertRun("team remove DemoR", CommandResult.STATE_ERROR);
        assertRun("team remove demor", CommandResult.STATE_ERROR);
    }

    @Test
    public void addPlayer() {
        final String playerName = MockUtils.getConstantPlayer().getName();
        this.teamManager.createTeam("Demo");
        assertRun(MockUtils.getConstantPlayer(), "team addPlayer " + playerName + " Demo");
        assertEquals(Collections.singletonList(playerName), this.teamManager.getTeam("Demo").getPlayers());
        assertRun("team addPlayer " + MockUtils.getConstantPlayer().getName() + " Demo", CommandResult.STATE_ERROR);
        assertRun(MockUtils.getConstantPlayer(), "team addPlayer velo demo", CommandResult.STATE_ERROR);
        assertRun(MockUtils.getConstantPlayer(), "team addPlayer velo Demo");

        final org.bukkit.scoreboard.Team scoreboardTeam = this.scoreboard.getTeam("Demo");
        assertEquals(new HashSet<>(Arrays.asList(playerName, "velo")), scoreboardTeam.getEntries(), "The players should have been added to the scoreboard team.");
        assertEquals(Component.text(playerName, scoreboardTeam.color()), MockUtils.getConstantPlayer().displayName());
    }

    @Test
    public void removePlayer() {
        final String playerName = MockUtils.getConstantPlayer().getName();
        final Team team = this.teamManager.createTeam("Demo");
        team.addPlayer(playerName);
        assertEquals(Collections.singletonList(playerName), team.getPlayers());
        assertRun(MockUtils.getConstantPlayer(), "team removePlayer " + MockUtils.getConstantPlayer().getName());
        assertEquals(Collections.emptyList(), team.getPlayers());
        assertEquals(Collections.emptySet(), this.scoreboard.getTeam(team.getName()).getEntries());
        assertRun("team removePlayer " + MockUtils.getConstantPlayer().getName(), CommandResult.STATE_ERROR);
    }

    @Test
    public void setColor() {
        final Team team = this.teamManager.createTeam("FallenKingdom");
        assertRun("team setColor FallenKingdom green");
        assertEquals(Color.VERT, team.getColor());
        //assertEquals(NamedTextColor.DARK_GREEN, this.scoreboard.getTeam(team.getName()).color()); // La tâche est décalée d'un tick, et il ne faudrait exécuter que celle-ci
        assertRun("team setColor unknown green", CommandResult.STATE_ERROR);
        assertRun("team setColor FallenKingdom idontknow");
        assertEquals(Color.BLANC, team.getColor());
        //assertEquals(NamedTextColor.WHITE, this.scoreboard.getTeam(team.getName()).color());
    }

    @Test
    public void rename() {
        final Team team = this.teamManager.createTeam("RenameMe");
        assertRun("team rename RenameMe Renamed");
        assertNull(this.teamManager.getTeam("RenameMe"));
        assertEquals(team, this.teamManager.getTeam("Renamed"));

        final Team team2 = this.teamManager.createTeam("typo");
        assertRun("team rename typo Renamed", CommandResult.STATE_ERROR);
        assertEquals(team, this.teamManager.getTeam("Renamed"));
        assertEquals(team2, this.teamManager.getTeam("typo"));
    }
}
