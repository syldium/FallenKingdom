package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Undo extends FkCommand
{
    public Undo()
    {
        super("undo", Messages.CMD_MAP_SCOREBOARD_UNDO, CommandPermission.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
    {
        if (!Fk.getInstance().getScoreboardManager().undo())
            throw new FkLightException(Messages.CMD_ERROR_SCOREBOARD_CANNOT_UNDO);
        return CommandResult.SUCCESS;
    }
}
