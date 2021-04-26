package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fkpi.team.FkTeam;
import fr.devsylone.fkpi.team.InTooManyTeamsException;
import fr.devsylone.fkpi.team.TeamChangeResult;
import fr.devsylone.fkpi.team.TeamManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TeamManagerTest extends AbstractTeamTest {

    @Test
    public void registerTeamAndPlayer() {
        final FkTeam team = builder().name("red").build();
        final TeamManager manager = new TeamManagerImpl(this.uuidService);
        assertTrue(manager.register(team));
        assertEquals(Optional.of(team), manager.find("red"), "The team should be registered.");

        final UUID uuid = UUID.randomUUID();
        assertEquals(TeamChangeResult.success(), team.addPlayer(uuid), "The player should be added to the team.");
        assertEquals(Optional.of(team), manager.playerTeam(uuid), "The player should be referred to this team.");
    }

    @Test
    public void registerTeamWithPlayersAlready() {
        final FkTeam team = builder("green")
                .playerNames("Goldfish", "Creeper")
                .build();
        final TeamManager manager = new TeamManagerImpl(this.uuidService);
        assertTrue(manager.register(team));

        final UUID player1 = this.uuidService.playerUniqueId("Goldfish");
        final UUID player2 = this.uuidService.playerUniqueId("Creeper");
        assertEquals(manager.playerTeam(player1), Optional.of(team), "The player's team should be known.");
        assertEquals(manager.playerTeam(player2), Optional.of(team));

        final UUID anotherPlayer = this.uuidService.playerUniqueId("Turtle");
        assertEquals(Optional.empty(), manager.playerTeam(anotherPlayer));
    }

    @Test
    public void playerInTwoTeamsResult() {
        final FkTeam blueTeam = builder("blue")
                .playerNames("Sheep", "Pig")
                .build();
        final TeamManager manager = new TeamManagerImpl(this.uuidService);
        assertTrue(manager.register(blueTeam));

        final FkTeam limeTeam = builder("lime").build();
        assertTrue(manager.register(limeTeam));
        assertEquals(TeamChangeResult.inTooManyTeams(), limeTeam.addPlayer("Sheep"), "The manager should block the insertion with the reason \"in too many teams\".");

        assertTrue(blueTeam.hasPlayer("Sheep"), "The player's team should not have changed.");
        assertFalse(limeTeam.hasPlayer("Sheep"));
    }

    @Test
    public void playerInTwoTeamsResultAlready() {
        final FkTeam orangeTeam = builder("orange")
                .playerNames("Watermelon", "Pumpkin")
                .build();
        final TeamManager manager = new TeamManagerImpl(this.uuidService);
        assertTrue(manager.register(orangeTeam));

        final FkTeam grayTeam = builder("gray")
                .playerNames("Pumpkin", "Granite")
                .build();
        assertThrows(InTooManyTeamsException.class, () -> manager.register(grayTeam));
        assertTrue(orangeTeam.hasPlayer("Pumpkin"));
    }

    @Test
    public void changePlayerTeam() {
        final FkTeam aquaTeam = builder("aqua").build();
        final TeamManager manager = new TeamManagerImpl(this.uuidService);
        assertTrue(manager.register(aquaTeam));

        final UUID uuid = this.uuidService.playerUniqueId("Husk");
        assertEquals(TeamChangeResult.success(), manager.changePlayerTeam(uuid, aquaTeam));
        assertTrue(aquaTeam.hasPlayer("Husk"));
        assertEquals(Optional.of(aquaTeam), manager.playerTeam(uuid));

        final FkTeam whiteTeam = builder("white").build();
        assertEquals(TeamChangeResult.success(), manager.changePlayerTeam(uuid, whiteTeam));
        assertFalse(aquaTeam.hasPlayer("Husk"));
        assertTrue(whiteTeam.hasPlayer("Husk"));
        assertEquals(Optional.empty(), manager.playerTeam(uuid));
    }

    @Test
    public void changePlayerTeam_alreadyIn() {
        final FkTeam team = builder("black").playerNames("Crab").build();
        final TeamManager manager = new TeamManagerImpl(this.uuidService);
        assertTrue(manager.register(team));

        final UUID uuid = this.uuidService.playerUniqueId("Crab");
        assertEquals(TeamChangeResult.alreadyIn(), manager.changePlayerTeam(uuid, team));
        assertEquals(Optional.of(team), manager.playerTeam(uuid));
    }

    @Test
    public void addPlayer_alreadyIn() {
        final FkTeam team = builder("purple").build();
        final TeamManager manager = new TeamManagerImpl(this.uuidService);
        assertTrue(manager.register(team));

        final UUID uuid = UUID.randomUUID();
        assertEquals(TeamChangeResult.success(), team.addPlayer(uuid));
        assertEquals(TeamChangeResult.alreadyIn(), team.addPlayer(uuid));
    }

    @Test
    public void unregister() {
        final FkTeam team = builder("cyan").color(NamedTextColor.AQUA).build();
        final TeamManager manager = new TeamManagerImpl(this.uuidService);
        manager.register(team);

        final UUID uuid = this.uuidService.playerUniqueId("Drowned");
        team.addPlayer(uuid);

        assertTrue(manager.unregister(team));
        assertEquals(Optional.empty(), manager.playerTeam(uuid));
        assertEquals(Optional.empty(), manager.find("cyan"));

        final FkTeam anotherCyan = builder("cyan").build();
        assertTrue(manager.register(anotherCyan));

        assertEquals(TeamChangeResult.success(), anotherCyan.addPlayer(uuid));
        assertTrue(team.removePlayer(uuid));
        assertEquals(Optional.of(anotherCyan), manager.playerTeam(uuid));

        assertEquals(TeamChangeResult.success(), team.addPlayer(uuid));
        assertEquals(Optional.of(anotherCyan), manager.playerTeam(uuid));
    }
}
