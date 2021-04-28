package fr.devsylone.fkpi.rule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

/**
 * A store of {@link Rule rules}.
 */
public interface RuleRegistry {

    <T> @Nullable T register(@NotNull Rule<T> rule, @NotNull T value);

    /**
     * Gets the {@link T value} for a {@link Rule rule}, if found.
     *
     * @param rule The rule
     * @param <T> The rule value type
     * @return The value or {@link Optional#empty()}
     */
    <T> @NotNull Optional<@NotNull T> findValue(@NotNull Rule<T> rule);

    @NotNull OptionalInt findIntValue(@NotNull Rule<Integer> rule);

    /**
     * Gets the {@link T value} for a {@link Rule rule}.
     *
     * @param rule The rule
     * @param <T> The rule value type
     * @return The value
     * @throws IllegalArgumentException If the rule is not registered
     */
    <T> @NotNull T value(@NotNull Rule<T> rule);

    /**
     * Gets an unmodifiable map of all registered rules.
     *
     * @return An unmodifiable map view.
     */
    @NotNull @UnmodifiableView Map<Rule<?>, Object> map();

    @NotNull @UnmodifiableView Set<@NotNull Rule<?>> registeredRules();
}
