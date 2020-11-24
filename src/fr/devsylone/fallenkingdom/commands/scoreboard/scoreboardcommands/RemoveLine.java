package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.ArgumentParser;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RemoveLine extends FkCommand
{
    public RemoveLine()
    {
        super("removeLine", "<i0;14:number>", Messages.CMD_MAP_SCOREBOARD_REMOVE_LINE, CommandPermission.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
    {
        int line = ArgumentParser.parseScoreboardLine(args.get(0), Messages.CMD_ERROR_SCOREBOARD_INVALID_LINE);
        if(!Fk.getInstance().getScoreboardManager().removeLine(line))
            throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_INVALID_LINE);
        return CommandResult.SUCCESS;
    }
}
