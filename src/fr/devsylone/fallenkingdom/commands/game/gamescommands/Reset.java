package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Reset extends FkGameCommand
{
	public Reset()
	{
		super("reset", "Enlève toutes les configurations du plugin.");
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(!Fk.getInstance().getCommandManager().isConfirmed("reset"))
		{
			String msg = "";
			msg = msg + ChatColor.RED + "§m--------------§c ATTENTION §c§m--------------\n";
			msg = msg + ChatColor.RED + "Vous êtes sur le point de supprimer\n";
			msg = msg + ChatColor.DARK_RED + "§ltous les réglages definis ! §cCela remettra le\n";
			msg = msg + ChatColor.RED + "plugin comme au premier lancement !\n";
			msg = msg + ChatColor.RED + "\n";
			msg = msg + ChatColor.RED + "Si c'est bien ce que vous voulez faire, \n";
			msg = msg + ChatColor.RED + "merci de retaper la commande.\n";
			msg = msg + ChatColor.RED + "§m--------------------------------------";

			fkp.sendMessage(msg);

			Fk.getInstance().getCommandManager().setConfirmed("reset", true);

		}
		else
		{
			broadcast(ChatColor.RED + "La partie a été " + ChatColor.DARK_RED + ChatColor.BOLD + "complètement" + ChatColor.RED + " réinitialisée");
			Fk.getInstance().reset();
			Fk.getInstance().getCommandManager().setConfirmed("reset", false);
		}
	}
}
