package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Stop extends FkGameCommand
{
	public Stop()
	{
		super("stop", "Arrête la partie après un lancement.");
		permission = ADMIN_PERMISSION;
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if (!Fk.getInstance().getGame().getState().equals(Game.GameState.BEFORE_STARTING))
		{
			if (!Fk.getInstance().getCommandManager().isConfirmed("stop"))
			{
				String msg = "";
				msg = msg + ChatColor.RED + "§m--------------§c ATTENTION §c§m--------------\n";
				msg = msg + ChatColor.RED + "Vous êtes sur le point de stopper\n";
				msg = msg + ChatColor.RED + "la partie en cours !\n";
				msg = msg + ChatColor.RED + "\n";
				msg = msg + ChatColor.RED + "Si c'est bien ce que vous voulez faire, \n";
				msg = msg + ChatColor.RED + "merci de retaper la commande.\n";
				msg = msg + ChatColor.RED + "§m--------------------------------------";

				fkp.sendMessage(msg);

				Fk.getInstance().getCommandManager().setConfirmed("stop", true);

			}
			else
			{
				Fk.getInstance().stop();
				super.broadcast(ChatColor.RED + "La partie a été stoppée");
			}
		}
		else
			throw new FkLightException("La partie n'est pas encore commencée !");
	}
}
