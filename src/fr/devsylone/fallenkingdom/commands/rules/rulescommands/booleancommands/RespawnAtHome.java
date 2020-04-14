package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class RespawnAtHome extends FkBooleanRuleCommand
{
	public RespawnAtHome()
	{
		super("respawnAtHome", Messages.CMD_MAP_RULES_RESPAWN_AT_HOME);
	}

	@Override
	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);

		if (Boolean.parseBoolean(args[0]))
			broadcast(Messages.CMD_RULES_RESPAWN_BASE.getMessage());
		else
			broadcast(Messages.CMD_RULES_RESPAWN_VANILLA.getMessage());
	}
}
