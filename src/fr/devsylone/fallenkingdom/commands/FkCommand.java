package fr.devsylone.fallenkingdom.commands;

import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.FkSound;

public abstract class FkCommand
{
	public static final String PLAYER_PERMISSION = "fallenkingdom.player";
	public static final String ADMIN_PERMISSION = "fallenkingdom.admin";

	protected int nbrArgs;
	protected String usage;
	protected String path;
	protected String description;
	protected String permission = PLAYER_PERMISSION;

	public FkCommand(String path, String args, int nbrArgs, String description)
	{
		this.path = path;
		if(Fk.getInstance().getConfig().getBoolean("translate-commands-args", true))
			for(Messages.Unit unit : Messages.Unit.values())
				args = args.replace(unit.getKey(), unit.tl(1));
		usage = ("/fk " + this.path + " " + args);

		this.nbrArgs = nbrArgs;
		this.description = description;
	}

	public abstract void execute(Player sender, FkPlayer fkp, String[] args) throws Exception;

	public int getNbrArgs()
	{
		return nbrArgs;
	}

	public String getUsage()
	{
		return usage;
	}

	protected void broadcast(String message, String prefix, FkSound sound)
	{
		Fk.broadcast(message, prefix, sound);
	}

	public String getDescription()
	{
		return description;
	}

	public String getPath()
	{
		return path;
	}

	public String getPermission()
	{
		return permission;
	}

	protected int assertPositiveNumber(String arg, boolean includeOrigin, Messages orElseMessage)
	{
		try {
			int value = Integer.parseInt(arg);
			if (includeOrigin ? value < 0 : value <= 0)
				throw new NumberFormatException(orElseMessage.getMessage());
			return value;
		} catch (NumberFormatException e) {
			throw new FkLightException(orElseMessage.getMessage());
		}
	}

	protected String createWarning(Messages warning, boolean format)
	{
		StringBuilder builder = new StringBuilder();
		if (format)
			builder.append("§c§m--------------§c ").append(Messages.WARNING.getMessage()).append("§c§m--------------\n");
		builder.append(warning.getMessage());
		if (format)
			builder.append("§c§m--------------------------------------");
		return builder.toString();
	}
}
