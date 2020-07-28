package fr.devsylone.fallenkingdom.fkboard.websocket.responses;

import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.rules.RuleValue;

public class RulesList implements Response {

    private final JsonArray rules;

    public RulesList(@Nonnull Map<Rule<?>, Object> rules) {
        this.rules = new Gson().toJsonTree(rules.entrySet().stream().map(rule -> {
            JsonObject object = new JsonObject();
            object.addProperty("name", rule.getKey().getName());
            try {
                object.addProperty("help", getHelpValue(rule.getKey()));
            } catch (IllegalArgumentException ignored) {

            }
            if (rule.getValue() instanceof RuleValue) {
                object.add("value", ((RuleValue) rule.getValue()).toJSON());
            } else {
                object.addProperty("value", rule.getValue().toString());
            }
            return object;
        }).collect(Collectors.toList())).getAsJsonArray();
    }

    private String getHelpValue(Rule<?> rule) throws IllegalArgumentException {
        switch (rule.getName()) {
            case "AllowedBlocks":
                return ChatColor.stripColor(Messages.CMD_MAP_RULES_ALLOW_BLOCK.getMessage());
            default:
                return ChatColor.stripColor(Messages.valueOf("CMD_MAP_RULES_" + rule.getName().replaceAll("(.)([A-Z])", "$1_$2").toUpperCase()).getMessage());
        }
    }

    @Override
    public int getStatusCode() {
        return 1002;
    }

    @Override
    public @Nonnull String toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("code", getStatusCode());
        object.add("rules", rules);
        return object.toString();
    }
}
