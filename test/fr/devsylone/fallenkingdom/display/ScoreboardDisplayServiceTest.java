package fr.devsylone.fallenkingdom.display;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScoreboardDisplayServiceTest {

    private final ScoreboardDisplayService scoreboard = new ScoreboardDisplayService("Title", asList("Line 1", "Line 2"));

    @Test
    public void withLine() {
        assertEquals(asList("Line 1", "Text"), this.scoreboard.withLine(1, "Text").lines());
        assertEquals(singletonList("Line 1"), this.scoreboard.withLine(1, null).lines());
        assertEquals(asList("Line 1", "Line 2", "", "Line 4"), this.scoreboard.withLine(3, "Line 4").lines());
        assertThrows(IllegalArgumentException.class, () -> this.scoreboard.withLine(2, null));
    }

    @Test
    public void reverseIndex() {
        assertEquals(1, this.scoreboard.reverseIndex(0));
        assertEquals(-2, this.scoreboard.reverseIndex(3));
    }
}
