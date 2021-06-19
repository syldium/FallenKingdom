package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.Argument;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.IntegerArgument;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VerticalLimit extends FkCommand {

    public VerticalLimit() {
        super("verticalLimit", Argument.list(new IntegerArgument("limit", true, "mettre a 0 pour ne pas en avoir", 0)), Messages.CMD_MAP_RULES_CHEST_LIMIT, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        int limit = ArgumentParser.parsePositiveInt(args.get(0), true, Messages.CMD_ERROR_NAN);
        plugin.getFkPI().getRulesManager().setRule(Rule.CHEST_LIMIT, limit);
        if(limit == 0)
            broadcast(Messages.CMD_RULES_VERTICAL_LIMIT_REMOVED.getMessage());
        else
            broadcast(Messages.CMD_RULES_VERTICAL_LIMIT_FIXED.getMessage().replace("%limit%", args.get(0)));
        return CommandResult.SUCCESS;
    }
}
