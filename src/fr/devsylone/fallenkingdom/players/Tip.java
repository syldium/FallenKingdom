package fr.devsylone.fallenkingdom.players;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.AbstractCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.utils.Messages;

import java.util.Optional;

public class Tip
{
	private AbstractCommand command = null;
	private final Messages tip;

	public Tip(Class<? extends AbstractCommand> cmd, Messages tip)
	{
		if (cmd != null) {
			Optional<? extends AbstractCommand> e = Fk.getInstance().getCommandManager().search(cmd);
			if (!e.isPresent()) {
				throw new RuntimeException("Manager don't have command " + cmd.getName() + " registered!");
			}
			command = e.get();
		}
		this.tip = tip;
	}

	public Class<? extends FkCommand> getCommandClass()
	{
		return null;
	}

	public String getTip()
	{
		return tip.getMessage();
	}

	public String getChatFormatted()
	{
		if(command != null)
		{
			String formatted = "  §c➤ §l§d/fk " + command.getFullUsage() + "\n";
			formatted = formatted + "     §b↪ " + tip.getMessage();
			return formatted;
		}

		else
		{
			return "§b" + tip.getMessage();
		}
	}
}
