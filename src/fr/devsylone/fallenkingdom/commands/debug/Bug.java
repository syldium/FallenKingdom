package fr.devsylone.fallenkingdom.commands.debug;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Bug extends FkCommand
{
	public Bug()
	{
		super("bug", "<message>", Messages.CMD_MAP_BUG, CommandPermission.PLAYER);
	}

	@Override
	public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label)
	{
		sender.sendMessage("Cette commande a été désactivée. Si tu souhaites signaler un bug, utilise ce lien : https://github.com/Etrenak/fallenkingdom/issues/new, "
				+ "ou bien rejoins notre discord : https://discord.gg/NwqFNa6");
		return CommandResult.SUCCESS;
	}
}
