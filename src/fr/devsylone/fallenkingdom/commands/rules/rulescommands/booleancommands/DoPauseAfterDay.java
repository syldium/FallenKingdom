package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class DoPauseAfterDay extends FkBooleanRuleCommand
{
	public DoPauseAfterDay()
	{
		super("doPauseAfterDay", Messages.CMD_MAP_RULES_DO_PAUSE_AFTER_DAY);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);
		broadcastOnOff(Boolean.parseBoolean(args[0]), Messages.CMD_RULES_DO_PAUSE_AFTER_DAY);
	}
}
