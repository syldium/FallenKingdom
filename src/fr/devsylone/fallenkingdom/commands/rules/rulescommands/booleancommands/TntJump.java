package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class TntJump extends FkBooleanRuleCommand
{
	public TntJump()
	{
		super("tntJump", Messages.CMD_MAP_RULES_TNT_JUMP);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);
		broadcastPossibleImpossible(Boolean.parseBoolean(args[0]), Messages.CMD_RULES_TNT_JUMP);
	}
}
