package fr.devsylone.fallenkingdom.commands.debug;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.DebuggerUtils;
import fr.devsylone.fallenkingdom.utils.Messages;

import org.bukkit.command.CommandSender;

import java.util.List;

public class Debug extends FkCommand
{
    public Debug()
    {
        super("debug", "[send]", null, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
    {
        boolean send = !args.isEmpty() && args.get(0).equalsIgnoreCase("send");
        boolean result = DebuggerUtils.debugGame(send, sender.getName());
        sender.sendMessage(result ? Messages.CMD_DEBUG_SUCCESS.getMessage() : Messages.CMD_DEBUG_ERROR.getMessage());
        plugin.getLogger().info("DEBUG DONE - send=" + send + " result=" + result + " username=" + sender.getName());
        return CommandResult.SUCCESS;
    }
}
