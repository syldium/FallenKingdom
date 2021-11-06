package fr.devsylone.fallenkingdom.display;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class TickFormatter {

    public static final int MINUTES_PER_HOUR = 60;
    public static final int TICKS_PER_SECOND = 20;
    public static final int TICKS_PER_GAME_DAY = 20 * MINUTES_PER_HOUR * TICKS_PER_SECOND;
    public static final int TICKS_PER_GAME_HOUR = TICKS_PER_GAME_DAY / 24;

    private final int startTimeDay;
    private final boolean twelveHours;
    private final boolean countdown;

    private static final String START_TIME_DAY = "start-time-day";
    private static final String TWELVE_HOURS = "twelve-hours";
    private static final String COUNTDOWN = "countdown";

    public TickFormatter() {
        this(6, false, false);
    }

    public TickFormatter(int startTimeDay, boolean twelveHours, boolean countdown) {
        this.startTimeDay = startTimeDay;
        this.twelveHours = twelveHours;
        this.countdown = countdown;
    }

    TickFormatter(@NotNull ConfigurationSection config) {
        this(config.getInt(START_TIME_DAY, 6), config.getBoolean(TWELVE_HOURS), config.getBoolean(COUNTDOWN));
    }

    public @NotNull String formatDays(long ticks) {
        return String.valueOf(ticks / TICKS_PER_GAME_DAY);
    }

    public @NotNull String formatHours(long ticks) {
        final int dayTicks = this.reverse((int) (ticks % TICKS_PER_GAME_DAY), TICKS_PER_GAME_DAY);
        final int hours = dayTicks / TICKS_PER_GAME_HOUR + this.startTimeDay;
        if (this.twelveHours) {
            final String period = hours < 12 ? "am" : "pm";
            final int hour = hours % 12;
            if (hour == 0) {
                return "12" + period;
            }
            return hour + period;
        }
        return twoDigits(hours);
    }

    public @NotNull String formatMinutes(long ticks) {
        final int hourTicks = this.reverse((int) (ticks % TICKS_PER_GAME_HOUR), TICKS_PER_GAME_HOUR);
        return twoDigits(hourTicks * MINUTES_PER_HOUR / TICKS_PER_GAME_HOUR);
    }

    public static @NotNull String twoDigits(int n) {
        if (n < 10) {
            return "0" + n;
        }
        return String.valueOf(n);
    }

    private int reverse(int n, int end) {
        return this.countdown ? end - n : n;
    }

    void save(@NotNull ConfigurationSection config) {
        config.set(START_TIME_DAY, this.startTimeDay);
        config.set(TWELVE_HOURS, this.twelveHours);
        config.set(COUNTDOWN, this.countdown);
    }
}
