package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.commands.CommandTest;
import fr.devsylone.fkpi.managers.RulesManager;
import fr.devsylone.fkpi.rules.Rule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuleCommandTest extends CommandTest {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void test() {
        RulesManager manager = MockUtils.getPluginMockSafe().getFkPI().getRulesManager();
        for (Rule<?> rule : Rule.values()) {
            Class<?> type = rule.getDefaultValue().getClass();
            Object value = manager.getRule(rule);
            Object otherValue = null;
            if (type.equals(Boolean.class)) {
                otherValue = !(boolean) value;
            } else if (type.equals(Integer.class)) {
                if (rule == Rule.DAY_DURATION) {
                    continue;
                }
                otherValue = (int) value == 10 ? 20 : 10;
            }

            if (otherValue != null) {
                assertRun("rules " + rule.getName() + " " + otherValue);
                assertEquals(otherValue, manager.getRule(rule), rule.getName() + " should be modified");
                manager.setRule((Rule) rule, value);
            }
        }
    }
}
