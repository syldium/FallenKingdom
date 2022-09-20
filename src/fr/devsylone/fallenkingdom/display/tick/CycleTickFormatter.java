package fr.devsylone.fallenkingdom.display.tick;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Formate les durées sur 12 ou 24 heures, synchronisé sur le cycle jour-nuit.
 * <p>
 * Un jour Minecraft correspond à un jour en jeu et l'heure en jeu dépend du
 * temps écoulé par rapport à la durée totale d'un jour pour une partie.
 */
public class CycleTickFormatter extends TickFormatter {

    public static final int HOURS_PER_DAY = 24;
    public static final int TICKS_PER_DAY_NIGHT_CYCLE = 24000;

    private final int startTimeDay;
    private final int ticksPerGameHour;
    private final float dayTickFactor;
    protected final boolean twelveHours;

    /**
     * Créé un affichage calqué sur la durée d'un jour Minecraft.
     */
    public CycleTickFormatter() {
        this(TICKS_PER_DAY_NIGHT_CYCLE, false, false, 6);
    }

    public CycleTickFormatter(int dayDuration, boolean twelveHours, boolean countdown, int startTimeDay) {
        super(dayDuration, countdown);
        this.twelveHours = twelveHours;
        this.startTimeDay = startTimeDay;
        this.ticksPerGameHour = dayDuration / HOURS_PER_DAY;
        this.dayTickFactor = (float) dayDuration / TICKS_PER_DAY_NIGHT_CYCLE;
    }

    public CycleTickFormatter(@NotNull ConfigurationSection config) {
        this(TICKS_PER_DAY_NIGHT_CYCLE, config.getBoolean(TWELVE_HOURS), config.getBoolean(COUNTDOWN), config.getInt(START_TIME_DAY, 6));
    }

    @Override
    public int extractHours(int ticks) {
        final int dayTicks = this.complementThenMod(ticks, this.dayDuration);
        return dayTicks / this.ticksPerGameHour + this.startTimeDay;
    }

    @Override
    public @NotNull String formatHours(int ticks) {
        final int hours = this.extractHours(ticks) % 24;
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

    @Override
    public int extractMinutes(int ticks) {
        final int hourTicks = this.complementThenMod(ticks, this.ticksPerGameHour);
        return hourTicks * 60 / this.ticksPerGameHour;
    }

    @Override
    public long worldTime(int days, int ticks) {
        return (long) days * TICKS_PER_DAY_NIGHT_CYCLE + (long) (ticks / this.dayTickFactor);
    }

    @Override
    public int timeFromWorld(long worldTime) {
        return (int) ((worldTime % TICKS_PER_DAY_NIGHT_CYCLE) * this.dayTickFactor);
    }

    @Override
    public int dayFromWorld(long worldTime) {
        return (int) (worldTime / TICKS_PER_DAY_NIGHT_CYCLE);
    }

    @Override
    public @NotNull TickFormatter withDayDuration(int dayDuration) {
        return new CycleTickFormatter(dayDuration, this.twelveHours, this.countdown, this.startTimeDay);
    }

    protected static final String START_TIME_DAY = "start-time-day";
    protected static final String TWELVE_HOURS = "twelve-hours";

    @Override
    public void save(@NotNull ConfigurationSection config) {
        super.save(config);
        config.set(TWELVE_HOURS, this.twelveHours);
        config.set(START_TIME_DAY, this.startTimeDay);
        config.set(TYPE, "cycle");
    }
}
