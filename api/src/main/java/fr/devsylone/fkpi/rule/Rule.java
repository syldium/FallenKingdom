package fr.devsylone.fkpi.rule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class Rule<T> {

    private static final Map<String, Rule<?>> INBUILT = new HashMap<>();

    public static final Rule<Integer> PVP_CAP = of("PvpCap", 3);
    public static final Rule<Integer> TNT_CAP = of("TntCap", 6);
    public static final Rule<Integer> NETHER_CAP = of("NetherCap", 1);
    public static final Rule<Integer> END_CAP = of("EndCap", 1);

    public static final Rule<Integer> CHEST_LIMIT = of("ChestLimit", 20);
    public static final Rule<Integer> DEATH_LIMIT = of("DeathLimit", 0);

    public static final Rule<Integer> CAPTURE_RATE = of("CaptureRate", 100);
    public static final Rule<Integer> DAY_DURATION = of("DayDuration", 24_000);

    public static final Rule<Boolean> DEEP_PAUSE = of("DeepPause", true);
    public static final Rule<Boolean> DO_PAUSE_AFTER_DAY = of("DoPauseAfterDay", false);
    public static final Rule<Boolean> ENDERPEARL_ASSAULT = of("EnderpearlAssault", true);
    public static final Rule<Boolean> ETERNAL_DAY = of("EternalDay", false);
    public static final Rule<Boolean> FRIENDLY_FIRE = of("FriendlyFire", true);
    public static final Rule<Boolean> HEALTH_BELOW_NAME = of("HealthBelowName", true);
    public static final Rule<Boolean> RESPAWN_AT_HOME = of("RespawnAtHome", false);

    public static final Rule<Character> GLOBAL_CHAT_PREFIX = of("GlobalChatPrefix", '!');

    public static final Rule<ChargedCreepersRule> CHARGED_CREEPERS = of("ChargedCreepers", ChargedCreepersRule.class, ChargedCreepersRule::new);
    public static final Rule<PlaceBlockInCaveRule> PLACE_BLOCK_IN_CAVE = of("PlaceBlockInCave", PlaceBlockInCaveRule.class, PlaceBlockInCaveRule::notActive);

    public static final List<Rule<Integer>> CAPS = Arrays.asList(Rule.END_CAP, Rule.NETHER_CAP, Rule.PVP_CAP, Rule.TNT_CAP);

    private final String name;
    private final Class<T> valueType;
    private final Supplier<T> defValue;

    public Rule(@NotNull String name, @NotNull Class<T> valueType, @NotNull Supplier<@NotNull T> defValue) {
        this.name = requireNonNull(name, "rule name");
        this.valueType = requireNonNull(valueType, "rule value type");
        this.defValue = requireNonNull(defValue, "rule default value");
    }

    public @NotNull String name() {
        return this.name;
    }

    public @NotNull Class<T> type() {
        return this.valueType;
    }

    public @NotNull Supplier<@NotNull T> defValue() {
        return this.defValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule<?> rule = (Rule<?>) o;
        return name.equals(rule.name) && valueType.equals(rule.valueType);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name + "Rule";
    }

    public static @Nullable Rule<?> findInbuilt(@NotNull String name) {
        return INBUILT.get(name);
    }

    private static @NotNull Rule<Integer> of(@NotNull String name, int defValue) {
        return of(name, Integer.class, () -> defValue);
    }

    private static @NotNull Rule<Boolean> of(@NotNull String name, boolean defValue) {
        return of(name, Boolean.class, () -> defValue);
    }

    private static @NotNull Rule<Character> of(@NotNull String name, char defValue) {
        return of(name, Character.class, () -> defValue);
    }

    private static <T> @NotNull Rule<T> of(@NotNull String name, @NotNull Class<T> valueType, @NotNull Supplier<@NotNull T> defValue) {
        final Rule<T> rule = new Rule<>(name, valueType, defValue);
        INBUILT.put(name, rule);
        return rule;
    }
}
