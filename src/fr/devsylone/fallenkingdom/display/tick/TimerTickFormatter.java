package fr.devsylone.fallenkingdom.display.tick;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Formate les durées comme un chronomètre, indépendant de la durée d'un cycle
 * jour-nuit.
 */
public class TimerTickFormatter extends TickFormatter {

    public static final int TICKS_PER_SECOND = 20;
    public static final int TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;

    public TimerTickFormatter(int dayDuration, boolean countdown) {
        super(dayDuration, countdown);
    }

    protected TimerTickFormatter(@NotNull ConfigurationSection config) {
        super(config);
    }

    @Override
    public int extractHours(int ticks) {
        final int dayTicks = this.complementThenMod(ticks, this.dayDuration);
        return dayTicks / TICKS_PER_MINUTE;
    }

    @Override
    public int extractMinutes(int ticks) {
        return this.complementThenMod(ticks, TICKS_PER_MINUTE) / TICKS_PER_SECOND;
    }

    @Override
    public long worldTime(int days, int ticks) {
        return (long) days * this.dayDuration + ticks;
    }

    @Override
    public int timeFromWorld(long worldTime) {
        return (int) (worldTime % this.dayDuration);
    }

    @Override
    public int dayFromWorld(long worldTime) {
        return (int) (worldTime / this.dayDuration);
    }

    @Override
    public @NotNull TickFormatter withDayDuration(int dayDuration) {
        return new TimerTickFormatter(dayDuration, this.countdown);
    }

    @Override
    public void save(@NotNull ConfigurationSection config) {
        super.save(config);
        config.set(TYPE, "timer");
    }
}
