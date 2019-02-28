package fr.devsylone.fallenkingdom.players;

import fr.devsylone.fallenkingdom.commands.FkCommand;

public class Tip
{
	private FkCommand command;
	private String tip;

	public Tip(FkCommand cmd, String tip)
	{
		command = cmd;
		this.tip = tip;
	}

	public FkCommand getCommand()
	{
		return command;
	}

	public String getTip()
	{
		return tip;
	}

	public String getChatFormatted()
	{
		if(command != null)
		{
			String formatted = "  §c➤ §l§d/fk " + command.getPath() + "\n";
			formatted = formatted + "     §b↪ " + tip.replace("&r", "§b");
			return formatted;
		}

		else
		{
			return "§b" + tip.replace("&r", "§b");
		}
	}
}
