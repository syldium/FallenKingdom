package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.display.tick.CycleTickFormatter;
import fr.devsylone.fallenkingdom.display.tick.TimerTickFormatter;
import fr.devsylone.fallenkingdom.display.tick.TickFormatter;
import org.junit.jupiter.api.Test;

import static fr.devsylone.fallenkingdom.display.tick.CycleTickFormatter.TICKS_PER_DAY_NIGHT_CYCLE;
import static fr.devsylone.fallenkingdom.display.tick.TimerTickFormatter.TICKS_PER_SECOND;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TickFormatterTest {

    @Test
    public void twelveHourClock() {
        final TickFormatter format = new CycleTickFormatter(TICKS_PER_DAY_NIGHT_CYCLE, true, false, 0);
        assertEquals("12am", format.formatHours(TICKS_PER_DAY_NIGHT_CYCLE));
        assertEquals("1am", format.formatHours(TICKS_PER_DAY_NIGHT_CYCLE / 24));
        assertEquals("12pm", format.formatHours(TICKS_PER_DAY_NIGHT_CYCLE / 2));
        assertEquals("11pm", format.formatHours(TICKS_PER_DAY_NIGHT_CYCLE - TICKS_PER_DAY_NIGHT_CYCLE / 24));
    }

    @Test
    public void militaryTime() {
        final TickFormatter format = new CycleTickFormatter();
        assertEquals("06", format.formatHours(TICKS_PER_DAY_NIGHT_CYCLE));
        assertEquals("07", format.formatHours(1000));
        assertEquals("18", format.formatHours(12000));
        assertEquals("05", format.formatHours(23125));

        final TickFormatter format2 = new TimerTickFormatter(TICKS_PER_DAY_NIGHT_CYCLE, false);
        assertEquals("01", format2.formatHours(1210));
        assertEquals("04", format2.formatMinutes(3680));
    }

    @Test
    public void cycleWorldTime() {
        final TickFormatter format1 = new CycleTickFormatter();
        final TickFormatter format2 = new CycleTickFormatter(8000, false, false, 0);
        assertEquals(5500L, format1.worldTime(0, 5500));
        assertEquals(16500L, format2.worldTime(0, 5500), "Each game tick should be equivalent to 3 ticks in the world.");
        assertEquals(5500, format2.timeFromWorld(16500L));

        assertEquals(52000L, format1.worldTime(2, 4000));
        assertEquals(60000L, format2.worldTime(2, 4000));
        assertEquals(4000, format1.timeFromWorld(52000L));
    }

    @Test
    public void timerWorldTime() {
        final TickFormatter format = new TimerTickFormatter(5000, false);
        assertEquals(5300L, format.worldTime(1, 300));
        assertEquals(300, format.timeFromWorld(5300L));
    }

    @Test
    public void cycleMinutes() {
        final TickFormatter format = new CycleTickFormatter(TICKS_PER_DAY_NIGHT_CYCLE, true, false, 10);
        assertEquals("00", format.formatMinutes(1000), "Should be 01:00");
        assertEquals("30", format.formatMinutes(2500), "Should be 02:30");
        assertEquals("15", format.formatMinutes(1250), "Should be 01:15");
        assertEquals("05", format.formatMinutes(85), "Should be 00:05"); // Comme tous les ticks entre 84 et 100 exclu
    }

    @Test
    public void timerCountdown() {
        final int thirtyMinutesInTicks = 30 * 60 * TICKS_PER_SECOND;
        final TickFormatter format = new TimerTickFormatter(thirtyMinutesInTicks, true);
        assertEquals(22, format.extractHours(8401), "Should be 22:59");
        assertEquals(56, format.extractMinutes(thirtyMinutesInTicks + TICKS_PER_SECOND * 4), "Should be 23:56");
    }
}
