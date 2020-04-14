package fr.devsylone.fallenkingdom.commands.teams.teamscommands;

import fr.devsylone.fallenkingdom.utils.Messages;
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
		super("setBase", "<team> <radius> [block] Ou item dans la main", 2, Messages.CMD_MAP_TEAM_SET_BASE);
	}

	@SuppressWarnings("deprecation")
	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		int radius;
		Material m;
		byte data = 0;
		if(!args[1].matches("\\d+") || (radius = Integer.parseInt(args[1])) <= 3)
			throw new FkLightException("Le rayon doit être un nombre supérieur à 3");

		if(args.length >= 3)
		{
			m = Material.matchMaterial(args[2]);
			if(m == null)
				throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_BLOCK.getMessage().replace("%block%", args[2]));
		}
		else
		{
			m = sender.getItemInHand().getType();
			// getData() implique d'initialiser le support des anciens Material à partir de la 1.15.2 (le charger dure entre 1 et 3 secondes)
			data = Fk.getInstance().isNewVersion() ? 0 : sender.getItemInHand().getData().getData();
		}

		if(!m.isBlock())
			throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_BLOCK.getMessage().replace("%block%", m.name().toLowerCase()));

		if(!Fk.getInstance().getFkPI().getTeamManager().getTeamNames().contains(args[0]))
			throw new FkLightException(Messages.CMD_ERROR_UNKNOWN_TEAM.getMessage().replace("%team%", args[0]));

		Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]).setBase(new Base(Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]), sender.getLocation(), radius, m, data));
		Base base = Fk.getInstance().getFkPI().getTeamManager().getTeam(args[0]).getBase();
		base.construct();
		broadcast("La base de l'équipe " + args[0] + " définie en :§b X > " + base.getCenter().getBlockX() + "; Z > " + base.getCenter().getBlockZ());
	}

}
