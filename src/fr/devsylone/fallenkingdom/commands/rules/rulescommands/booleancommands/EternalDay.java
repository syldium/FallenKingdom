package fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.commands.rules.FkBooleanRuleCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class EternalDay extends FkBooleanRuleCommand
{
	public EternalDay()
	{
		super("eternalDay", Messages.CMD_MAP_RULES_ETERNAL_DAY);
	}

	@SuppressWarnings("deprecated")
	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		setRuleValue(args[0]);
		for(World w : Bukkit.getWorlds())
		{
			w.setGameRuleValue("doDaylightCycle", Boolean.parseBoolean(args[0]) ? "false" : "true");
			w.setTime(Fk.getInstance().getGame().getExceptedWorldTime());
		}
		broadcastOnOff(Boolean.parseBoolean(args[0]), Messages.CMD_RULES_ETERNAL_DAY);
		broadcast("La nuit est maintenant", (Boolean.parseBoolean(args[0]) ? "dés" : "") + "activée", " !");
	}
}
