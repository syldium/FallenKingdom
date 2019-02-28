package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.teams.Base;

public class SetBase extends fr.devsylone.fallenkingdom.commands.teams.FkTeamCommand
{
	public SetBase()
	{
		super("SetBase", "<team> <radius> [block] Ou item dans la main", 2, "Modifier l'emplacement de la base d'une équipe.");
	}

	@SuppressWarnings("deprecation")
	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		int radius = 0;
		Material m = Material.AIR;
		byte data = 0;
		if(!args[1].matches("\\d+") || (radius = Integer.parseInt(args[1])) <= 3)
			throw new FkLightException("Le rayon doit être un nombre supérieur à 3");

		if(args.length >= 3)
		{
			m = Material.matchMaterial(args[2]);
			if(m == null)
				throw new FkLightException("\"" + args[2] + "\" n'est pas un bloc ! ");
		}
		else
		{
			m = sender.getItemInHand().getType();
			data = sender.getItemInHand().getData().getData();
		}
		
		if(!Fk.getInstance().getFkPI().getTeamManager().getTeamNames().contains(args[0]))
			throw new FkLightException("L'équipe n'existe pas !");

		Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]).setBase(new Base(Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]), sender.getLocation(), radius, m, data));
		Base base = Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]).getBase();
		base.construct();
		broadcast("La base de l'équipe " + args[0] + " définie en :§b X > " + base.getCenter().getBlockX() + "; Z > " + base.getCenter().getBlockZ());
	}

}
