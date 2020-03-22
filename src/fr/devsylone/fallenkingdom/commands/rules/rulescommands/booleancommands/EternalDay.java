package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.Fk;
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
		for(World w : Bukkit.getWorlds())
		{
			w.setGameRuleValue("doDaylightCycle", Boolean.valueOf(args[0]).booleanValue() ? "false" : "true");
			w.setTime(Fk.getInstance().getGame().getExceptedWorldTime());
		}
		broadcast("La nuit est maintenant", (Boolean.valueOf(args[0]).booleanValue() ? "dés" : "") + "activée", " !");
	}
}
