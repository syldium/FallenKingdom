package fr.devsylone.fallenkingdom.rule;

import fr.devsylone.fkpi.rule.Rule;
import fr.devsylone.fkpi.rule.RuleRegistry;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleRegistryTest {

    @Test
    public void notFound() {
        final String noValueFound = "No value should be found.";
        final RuleRegistry registry = new RuleRegistryImpl();
        registry.register(Rule.END_CAP, 4);
        assertEquals(Optional.empty(), registry.findValue(Rule.NETHER_CAP), noValueFound);
    }

    @Test
    public void register() {
        final String valueFound = "A value should be found.";
        final RuleRegistry registry = new RuleRegistryImpl();
        registry.register(Rule.TNT_CAP, 7);
        assertEquals(Optional.of(7), registry.findValue(Rule.TNT_CAP), valueFound);
        assertEquals(7, registry.value(Rule.TNT_CAP), valueFound);
    }

    @Test
    public void registerIfAbsent() {
        final RuleRegistry registry = new RuleRegistryImpl();
        assertEquals(Optional.empty(), registry.findValue(Rule.DEEP_PAUSE));
        assertEquals(Rule.DEEP_PAUSE.defValue(), registry.value(Rule.DEEP_PAUSE));
    }
}
