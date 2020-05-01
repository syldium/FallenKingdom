package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DayDuration extends FkCommand
{
    public DayDuration()
    {
        super("dayDuration", "<i1:mins>", Messages.CMD_MAP_RULES_DAY_DURATION, CommandPermission.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
    {
        int mins = ArgumentParser.parsePositiveInt(args.get(0), false, Messages.CMD_ERROR_DAY_DURATION_FORMAT);
        if(mins >= 1200)
            mins /= 1200;

        FkPI.getInstance().getRulesManager().setRule(Rule.DAY_DURATION, mins*1200);
        broadcast(Messages.CMD_RULES_DAY_DURATION.getMessage()
                .replace("%duration%", String.valueOf(mins))
                .replace("%unit%", Messages.Unit.MINUTES.tl(mins))
        );
        plugin.getGame().updateDayDuration();
        return CommandResult.SUCCESS;
    }
}
