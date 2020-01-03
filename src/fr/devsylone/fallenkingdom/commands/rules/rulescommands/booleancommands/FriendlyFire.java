package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class FriendlyFire extends FkBooleanRuleCommand
{
	public FriendlyFire()
	{
		super("friendlyFire", "À true, les joueurs au sein d'une même équipe peuvent s'infliger des dégâts.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);
		broadcast("Le FriendlyFire est maintenant", (Boolean.valueOf(args[0]).booleanValue() ? "" : "dés") + "activé", " ! ");
	}
}
