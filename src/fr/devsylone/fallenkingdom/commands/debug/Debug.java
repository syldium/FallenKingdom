package fr.devsylone.fallenkingdom.commands.debug;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.DebuggerUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Debug extends FkCommand
{
    public Debug()
    {
        super("debug",null, CommandPermission.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
    {
        DebuggerUtils.debugGame();
        sender.sendMessage("Done");
        return CommandResult.SUCCESS;
    }

    @Override
    public boolean shouldDisplay()
    {
        return false;
    }
}
