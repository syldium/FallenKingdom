package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.ArgumentParseException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CaptureRate extends FkCommand {

    public CaptureRate() {
        super("captureRate", "<rate>", Messages.CMD_MAP_RULES_CAPTURE_RATE, CommandPermission.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        int rate = ArgumentParser.parsePercentage(args.get(0), Messages.CMD_ERROR_PERCENTAGE_FORMAT);
        if (rate < 1) {
            throw new ArgumentParseException(Messages.CMD_ERROR_NAN.getMessage());
        }
        plugin.getFkPI().getRulesManager().setRule(Rule.CAPTURE_RATE, rate);
        broadcast(Messages.CMD_RULES_CAPTURE_RATE_SET.getMessage().replace("%rate%", args.get(0)));
        return CommandResult.SUCCESS;
    }
}
