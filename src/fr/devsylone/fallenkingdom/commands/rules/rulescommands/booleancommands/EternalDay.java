package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class EternalDay extends FkBooleanRuleCommand
{
	public EternalDay()
	{
		super("eternalDay", "À true, désactive le cycle jour/nuit.");
	}

	@SuppressWarnings("deprecated")
	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);
		if(Boolean.valueOf(args[0]).booleanValue())
		{
			for(World w : Bukkit.getWorlds())
			{
				w.setGameRuleValue("doDaylightCycle", "false");
				w.setTime(12000L);
			}
		}
		else
		{
			for(World w : Bukkit.getWorlds())
				w.setGameRuleValue("doDaylightCycle", "true");
		}
		broadcast("La nuit est maintenant", (Boolean.valueOf(args[0]).booleanValue() ? "dés" : "") + "activée", " !");
	}
}
