package fr.devsylone.fallenkingdom.fkboard.websocket.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.AbstractCommand;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.ArgumentParseException;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.fkboard.websocket.FkBoardWebSocket;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.RuleChangeEvent;
import fr.devsylone.fkpi.rules.AllowedBlocks;
import fr.devsylone.fkpi.rules.DisabledPotions;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.BlockDescription;
import fr.devsylone.fkpi.util.XPotionData;

class EditRuleCommand extends WSCommand {

    EditRuleCommand() {
        super("edit rule", true, "rule", "value");
    }

    @Override
    public boolean execute(Fk plugin, FkPI fkpi, FkBoardWebSocket webSocket, JsonObject json) {
        Rule<?> rule = Rule.getByName(json.get("rule").getAsString());
        if (rule == null) {
            return false;
        }

        switch (rule.getName()) {
            case "AllowedBlocks":
                if (!json.get("value").isJsonArray()) {
                    return false;
                }
                AllowedBlocks allowedBlocks = fkpi.getRulesManager().getRule(Rule.ALLOWED_BLOCKS);
                allowedBlocks.getValue().clear();
                for (BlockDescription bd : parseAllowedBlocks(json.get("value").getAsJsonArray())) {
                    allowedBlocks.getValue().add(bd);
                }
                Bukkit.getPluginManager().callEvent(new RuleChangeEvent<>(Rule.ALLOWED_BLOCKS, allowedBlocks));
                return true;
            case "DisabledPotions":
                if (!json.get("value").isJsonArray()) {
                    return false;
                }
                DisabledPotions disabledPotions = fkpi.getRulesManager().getRule(Rule.DISABLED_POTIONS);
                disabledPotions.getValue().clear();
                for (XPotionData potion : parseDisabledPotions(json.get("value").getAsJsonArray())) {
                    disabledPotions.disablePotion(potion);
                }
                return true;
            default:
                List<String> args = new ArrayList<>();
                args.add(json.get("rule").getAsString());
                args.addAll(Arrays.asList(json.get("value").getAsString().split(" ")));
                FkRuleCommand command = (FkRuleCommand)  Fk.getInstance().getCommandManager().search(FkRuleCommand.class).orElseThrow(() -> new RuntimeException("Can't get rule"));
                AbstractCommand c = command.get(args);
                if (c.equals(command) || !c.isValidExecutor(Bukkit.getConsoleSender())) {
                    return false;
                }
                try {
                    c.execute(Fk.getInstance(), Bukkit.getConsoleSender(), args, "FkBoard");
                } catch (ArgumentParseException e) {
                    plugin.getLogger().warning("Invalid rule data sent by fkboard (" + e.getMessage() + ")");
                } catch (FkLightException e) {
                    plugin.getLogger().warning("Cannot change " + rule.getName() + " rule (" + e.getMessage() + ")");
                }
                return true;
        }
    }

    private List<BlockDescription> parseAllowedBlocks(JsonArray value) {
        List<BlockDescription> allowedBlocks = new ArrayList<>();
        for (JsonElement block : value) {
            BlockDescription bd = new BlockDescription(block.getAsString());
            if (Material.matchMaterial(bd.getBlockName()) != null) {
                allowedBlocks.add(bd);
            }
        }
        return allowedBlocks;
    }

    private List<XPotionData> parseDisabledPotions(JsonArray value) {
        List<XPotionData> disabledPotions = new ArrayList<>();
        for (JsonElement potion : value) {
            try {
                disabledPotions.add(new XPotionData(PotionType.valueOf(potion.getAsString()), false, false));
            } catch (IllegalArgumentException ignored) {

            }
        }
        return disabledPotions;
    }
}
