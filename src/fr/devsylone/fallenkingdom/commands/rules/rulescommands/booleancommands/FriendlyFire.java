package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class FriendlyFire extends FkBooleanRuleCommand
{
	public FriendlyFire()
	{
		super("friendlyFire", Messages.CMD_MAP_RULES_FRIENDLY_FIRE);
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);
		broadcastOnOff(Boolean.parseBoolean(args[0]), Messages.CMD_RULES_FRIENDLY_FIRE);
	}
}
