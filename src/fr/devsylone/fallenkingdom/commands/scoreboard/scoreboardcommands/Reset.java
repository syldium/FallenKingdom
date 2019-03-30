package fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.scoreboard.FkScoreboardCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Reset extends FkScoreboardCommand
{
	public Reset()
	{
		super("Reset", "", 0, "Réinitialise l'agencement du scoreboard");
	}

	public void execute(final Player sender, final FkPlayer fkp, String[] args)
	{
		if(!Fk.getInstance().getCommandManager().isConfirmed("sbreset"))
		{
			String msg = "";
			msg = msg + ChatColor.RED + "§m--------------§c ATTENTION §c§m--------------\n";
			msg = msg + ChatColor.RED + "Vous êtes sur le point de supprimer\n";
			msg = msg + ChatColor.DARK_RED + "tous les réglages §ldu scoreboard§c\n\n";
			msg = msg + ChatColor.RED + "Si c'est bien ce que vous voulez faire, \n";
			msg = msg + ChatColor.RED + "merci de retaper la commande.\n";
			msg = msg + ChatColor.RED + "§m--------------------------------------";

			fkp.sendMessage(msg);

			Fk.getInstance().getCommandManager().setConfirmed("sbreset", true);
		}
		else
		{
			fkp.sendMessage("§aLe scoreboard a été réinitialisé !");
			Fk.getInstance().getScoreboardManager().reset();
			Fk.getInstance().getScoreboardManager().recreateAllScoreboards();
			Fk.getInstance().getScoreboardManager().refreshAllScoreboards();
		}
	}
}
