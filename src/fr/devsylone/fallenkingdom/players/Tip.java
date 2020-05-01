package fr.devsylone.fallenkingdom.players;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.AbstractCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;

import java.util.Optional;

public class Tip
{
	private AbstractCommand command = null;
	private final String tip;

	public Tip(Class<? extends AbstractCommand> cmd, String tip)
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
		return tip;
	}

	public String getChatFormatted()
	{
		if(command != null)
		{
			String formatted = "  §c➤ §l§d/fk " + command.getUsage() + "\n";
			formatted = formatted + "     §b↪ " + tip.replace("&r", "§b");
			return formatted;
		}

		else
		{
			return "§b" + tip.replace("&r", "§b");
		}
	}
}
