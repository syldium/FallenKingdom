package fr.devsylone.fallenkingdom.display;

import org.junit.jupiter.api.Test;

import static fr.devsylone.fallenkingdom.display.TickFormatter.TICKS_PER_GAME_DAY;
import static fr.devsylone.fallenkingdom.display.TickFormatter.TICKS_PER_GAME_HOUR;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TickFormatterTest {

    @Test
    public void twelveHourClock() {
        final TickFormatter format = new TickFormatter(0, true, false);
        assertEquals("12am", format.formatHours(TICKS_PER_GAME_DAY ));
        assertEquals("1am", format.formatHours(TICKS_PER_GAME_HOUR));
        assertEquals("12pm", format.formatHours(TICKS_PER_GAME_DAY / 2));
        assertEquals("11pm", format.formatHours(TICKS_PER_GAME_DAY - TICKS_PER_GAME_HOUR));
    }

    @Test
    public void militaryTime() {
        final TickFormatter format = new TickFormatter(0, false, false);
        assertEquals("00", format.formatHours(TICKS_PER_GAME_DAY));
        assertEquals("01", format.formatHours(TICKS_PER_GAME_HOUR));
        assertEquals("12", format.formatHours(TICKS_PER_GAME_DAY / 2));
    }

    @Test
    public void militaryTimeOffset() {
        final TickFormatter format = new TickFormatter(6, false, false);
        assertEquals("07", format.formatHours(TICKS_PER_GAME_HOUR));
    }

    @Test
    public void minutes() {
        final TickFormatter format = new TickFormatter(6, true, false);
        assertEquals("00", format.formatMinutes(TICKS_PER_GAME_HOUR));
        assertEquals("30", format.formatMinutes(TICKS_PER_GAME_HOUR * 2 + TICKS_PER_GAME_HOUR / 2));
        assertEquals("15", format.formatMinutes(TICKS_PER_GAME_HOUR / 4));
        assertEquals("05", format.formatMinutes(90));
    }
}
