package fr.devsylone.fallenkingdom.fkboard.websocket.responses;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.rules.RuleValue;

public class RuleChange implements Response {

    private final JsonObject rule;

    public <T> RuleChange(@Nonnull Rule<T> rule, @Nonnull T value) {
        this.rule = new JsonObject();
        this.rule.addProperty("rule", rule.getName());
        if (value instanceof RuleValue) {
            this.rule.add("value", ((RuleValue) value).toJSON());
        } else {
            this.rule.addProperty("value", value.toString());
        }
    }

    @Override
    public int getStatusCode() {
        return 1003;
    }

    @Override
    public @Nonnull String toJSON() {
        rule.addProperty("code", getStatusCode());
        return rule.toString();
    }
}
