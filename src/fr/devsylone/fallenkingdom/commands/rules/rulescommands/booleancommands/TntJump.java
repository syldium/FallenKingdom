package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class TntJump extends FkBooleanRuleCommand
{
	public TntJump()
	{
		super("tntJump", "Autorise ou non le fait de sauter d'une tnt à une autre aux abords d'une base adverse.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);
		broadcast("Il est maintenant", Boolean.parseBoolean(args[0]) ? "possible" : "impossible",
				"de sauter d'une tnt à une autre aux abords d'un base adverse");
	}
}
