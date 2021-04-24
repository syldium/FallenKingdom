package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fkpi.team.FkTeam;
import fr.devsylone.fkpi.team.TeamChangeResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static fr.devsylone.fallenkingdom.team.TeamBuilderImpl.DEFAULT_COLOR;
import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TeamTest extends AbstractTeamTest {

    @Test
    public void builder_defaultValues() {
        assertThrows(NullPointerException.class, () -> builder().build());
        final FkTeam team = builder().name("demo").build();
        assertEquals("demo", team.name());
        assertEquals(DEFAULT_COLOR, team.color());
        assertEquals(text("demo", DEFAULT_COLOR), team.asComponent());
        assertTrue(team.isEmpty());
        assertTrue(team.playersUniqueIds().isEmpty());
    }

    @Test
    public void builder_displayNameColor() {
        final TextColor color = TextColor.color(0xFFBB00);
        final FkTeam team = builder().name("test").color(color).build();
        assertEquals(text("test", color), team.asComponent());
    }

    @Test
    public void builder_customDisplayName() {
        final Component displayName = text("displayName", TextColor.color(0x00E020));
        final FkTeam team = builder("team")
                .displayName(displayName)
                .color(NamedTextColor.BLUE)
                .build();
        assertEquals(displayName, team.asComponent());
        assertEquals(NamedTextColor.BLUE, team.color());
    }

    @Test
    public void builder_nameToUUID() {
        final FkTeam team = builder("pink").playerNames("Hoglin").build();
        assertTrue(team.playersNames().contains("Hoglin"));
        final UUID uuid = this.uuidService.playerUniqueId("Hoglin");
        assertTrue(team.playersUniqueIds().contains(uuid));
    }

    @Test
    public void addPlayer() {
        final FkTeam team = builder().name("cyan").build();
        assertEquals(TeamChangeResult.success(), team.addPlayer("Sponge"));
        assertTrue(team.hasPlayer("Sponge"));
    }
}
