package fr.devsylone.fallenkingdom.rule;

import fr.devsylone.fkpi.rule.Rule;
import fr.devsylone.fkpi.rule.RuleRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiPredicate;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unchecked", "rawtypes"})
public class RuleRegistryImpl implements RuleRegistry {

    private final Map<Rule<?>, Object> rules = new LinkedHashMap<>();
    private final BiPredicate<Rule<?>, Object> ruleChange;

    public <T> RuleRegistryImpl(@NotNull BiPredicate<Rule<T>, T> ruleChange) {
        this.ruleChange = (BiPredicate) ruleChange;
    }

    public RuleRegistryImpl() {
        this.ruleChange = (rule, value) -> true;
    }

    @Override
    public <T> @Nullable T register(@NotNull Rule<T> rule, @NotNull T value) {
        requireNonNull(rule, "rule");
        requireNonNull(value, "rule value");
        if (this.ruleChange.test(rule, value)) {
            return (T) this.rules.put(rule, value);
        } else {
            return (T) this.rules.get(rule);
        }
    }

    @Override
    public @NotNull <T> Optional findValue(@NotNull Rule<T> rule) {
        return Optional.ofNullable(this.rules.get(rule));
    }

    @Override
    public @NotNull OptionalInt findIntValue(@NotNull Rule<Integer> rule) {
        Integer value = (Integer) this.rules.get(rule);
        return value == null ? OptionalInt.empty() : OptionalInt.of(value);
    }

    @Override
    public <T> @NotNull T value(@NotNull Rule<T> rule) {
        return (T) this.rules.computeIfAbsent(rule, Rule::defValue);
    }

    @Override
    public @NotNull Map<Rule<?>, Object> map() {
        return Collections.unmodifiableMap(this.rules);
    }

    @Override
    public @NotNull Set<@NotNull Rule<?>> registeredRules() {
        return Collections.unmodifiableSet(this.rules.keySet());
    }
}
