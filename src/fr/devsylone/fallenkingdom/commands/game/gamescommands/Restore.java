package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.FkSound;

public class Restore extends FkGameCommand
{
	public Restore()
	{
		super("restore", "[pause_id] (Par défaut restaure à la dernière pause)", 0, "Permet de restaurer les états des joueurs avant une pause.");
		permission = ADMIN_PERMISSION;
	}

	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(!Fk.getInstance().getGame().getState().equals(Game.GameState.PAUSE))
			throw new FkLightException("La partie n'est pas en pause");
		
		int id = -1;
		if(args.length > 0)
			try
			{
				id = Integer.parseInt(args[0]);
			}catch(NumberFormatException e)
			{
				throw new FkLightException(args[0] + " n'est pas un id valide");
			}
		id = Fk.getInstance().getPauseRestorer().restoreAll(id);//Si l'id était -1 ça remet le bon
		Fk.broadcast("§2Votre inventaire, position, niveaux d'experience, barre de faim, barre de vie, ainsi que vos effets de potions ont été restaurés comme au début de la pause n°" + id, FkSound.NOTE_PLING);
	}
}
