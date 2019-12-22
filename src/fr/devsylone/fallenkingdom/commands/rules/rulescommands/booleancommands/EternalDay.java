package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class EternalDay extends FkBooleanRuleCommand
{
	public EternalDay()
	{
		super("eternalDay", "A true, désactive de la nuit.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);
		if(Boolean.valueOf(args[0]).booleanValue())
		{
			for(World w : Bukkit.getWorlds())
			{
				w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
				w.setTime(12000L);
			}
		}
		else
		{
			for(World w : Bukkit.getWorlds())
				w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		}
		broadcast("La nuit est maintenant", (Boolean.valueOf(args[0]).booleanValue() ? "dés" : "") + "activée", " !");
	}
}
