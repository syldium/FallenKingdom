package fr.devsylone.fallenkingdom.display.tick;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import static fr.devsylone.fallenkingdom.display.tick.CycleTickFormatter.TICKS_PER_DAY_NIGHT_CYCLE;

/**
 * Formate l'affichage de ticks de jeu en heures et minutes.
 */
public abstract class TickFormatter {

    protected final int dayDuration;
    protected final boolean countdown;

    /**
     * Créée un nouveau formateur.
     *
     * @param dayDuration Durée d'un jour de jeu en ticks (24 000 ticks pour un cycle jour-nuit Minecraft)
     * @param countdown Si le temps est affiché comme un décompte avant la fin du jour
     */
    public TickFormatter(int dayDuration, boolean countdown) {
        this.dayDuration = dayDuration;
        this.countdown = countdown;
    }

    protected TickFormatter(@NotNull ConfigurationSection config) {
        this(TICKS_PER_DAY_NIGHT_CYCLE, config.getBoolean(COUNTDOWN));
    }

    /**
     * Convertit une durée en un nombre d'heures.
     *
     * @param ticks Durée en ticks depuis le début du jour/monde
     * @return Nombre d'heures
     */
    public abstract int extractHours(int ticks);

    public @NotNull String formatHours(int ticks) {
        return twoDigits(this.extractHours(ticks) % 24);
    }

    /**
     * Convertit une durée en un nombre de minutes sans les heures.
     *
     * @param ticks Durée en ticks depuis le début du jour
     * @return Nombre de minutes
     */
    public abstract @Range(from = 0, to = 59) int extractMinutes(int ticks);

    public @NotNull String formatMinutes(int ticks) {
        return twoDigits(this.extractMinutes(ticks));
    }

    public abstract long worldTime(int days, int ticks);

    public abstract int timeFromWorld(long worldTime);

    public abstract int dayFromWorld(long worldTime);

    public int dayDuration() {
        return this.dayDuration;
    }

    @Contract(value = "_ -> new")
    public abstract @NotNull TickFormatter withDayDuration(int dayDuration);

    protected int complementThenMod(int ticks, int mod) {
        final int r = ticks % mod;
        return this.countdown ? mod - r : r;
    }

    public static @NotNull String twoDigits(int n) {
        if (n < 10) {
            return "0" + n;
        }
        return String.valueOf(n);
    }

    protected static final String TYPE = "type";
    protected static final String COUNTDOWN = "countdown";

    public static @NotNull TickFormatter fromConfig(@NotNull ConfigurationSection config) {
        if (config.getString(TYPE, "cycle").equals("timer")) {
            return new TimerTickFormatter(config);
        }
        return new CycleTickFormatter(config);
    }

    @MustBeInvokedByOverriders
    public void save(@NotNull ConfigurationSection config) {
        config.set(COUNTDOWN, this.countdown);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' +
                "dayDuration=" + this.dayDuration +
                ", countdown=" + this.countdown +
                '}';
    }
}
