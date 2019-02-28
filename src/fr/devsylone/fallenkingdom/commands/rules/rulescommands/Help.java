package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Help extends FkRuleCommand
{
	public Help()
	{
		super("help", "", 0, "Ben c'est ça :D.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		Player p = sender;
		Fk.getInstance().getCommandManager().sendHelp("rules", p);
	}
}	
