package fr.devsylone.fallenkingdom.commands.rules.rulescommands.capcommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkCapCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class TntCap extends FkCapCommand
{
	public TntCap()
	{
		super("tntCap", Messages.CMD_MAP_RULES_TNT_CAP);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		executeCap(args[0], Messages.CMD_RULES_CAP_TNT);
	}
}
