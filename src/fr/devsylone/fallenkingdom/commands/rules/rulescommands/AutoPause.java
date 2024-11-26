package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AutoPause extends FkCommand {

    public AutoPause() {
        super("autoPause", "<afterDay|afterCapture> <true|false>", Messages.CMD_MAP_RULES_AUTO_PAUSE, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) throws FkLightException, IllegalArgumentException {
        final boolean value = Boolean.parseBoolean(args.get(1));
        if ("afterDay".equalsIgnoreCase(args.get(0))) {
            plugin.getFkPI().getRulesManager().getRule(Rule.AUTO_PAUSE).setAfterDay(value);
            broadcastState(value, Messages.CMD_RULES_AUTO_PAUSE_AFTER_DAY, Messages.CMD_RULES_ACTIVATED, Messages.CMD_RULES_DEACTIVATED);
        } else if ("afterCapture".equalsIgnoreCase(args.get(0))) {
            plugin.getFkPI().getRulesManager().getRule(Rule.AUTO_PAUSE).setAfterCapture(value);
            broadcastState(value, Messages.CMD_RULES_AUTO_PAUSE_AFTER_CAPTURE, Messages.CMD_RULES_ACTIVATED, Messages.CMD_RULES_DEACTIVATED);
        } else {
            throw new IllegalArgumentException("Invalid argument: " + args.get(0));
        }
        return CommandResult.SUCCESS;
    }
}
