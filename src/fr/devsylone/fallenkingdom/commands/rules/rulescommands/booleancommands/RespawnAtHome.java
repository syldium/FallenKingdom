package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class RespawnAtHome extends FkBooleanRuleCommand
{
	public RespawnAtHome()
	{
		super("RespawnAtHome", "Si le joueur a un lit il respawn normalement, sinon True=Respawn dans sa base, False=Respawn au WorldSpawn");
	}

	@Override
	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);
		broadcast("Lors d'une mort, les joueurs réapparaissent", (Boolean.valueOf(args[0]).booleanValue() ? "à leur base" : "au WorldSpawn"), " ! ");
	}

}
