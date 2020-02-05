package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class Create extends FkTeamCommand
{
	public Create()
	{
		super("create", "<team>", 1, "Crée une équipe.");
		permission = ADMIN_PERMISSION;
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(!Fk.getInstance().getFkPI().getTeamManager().createTeam(args[0]))
		{
			String msg = "";
			msg = msg + ChatColor.RED + "§m--------------§c ATTENTION §c§m--------------\n";
			msg = msg + ChatColor.DARK_AQUA + " Le nom de l'équipe ne fait pas parti\n";
			msg = msg + ChatColor.DARK_AQUA + " des couleurs gérées par le plugin.\n";
			msg = msg + ChatColor.DARK_AQUA + " L'équipe a donc reçu la couleur blanche.\n";
			msg = msg + ChatColor.DARK_AQUA + " §e/fk team SetColor " + ChatColor.DARK_AQUA + "pour modifier.\n";
			msg = msg + ChatColor.RED + "§m--------------------------------------";

			fkp.sendMessage(msg);
		}

		broadcast("L'équipe " + Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]).toString() + " §6a été créée !");
	}
}
