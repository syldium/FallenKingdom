package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.FkGameCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.game.Game;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;

public class Pause extends FkGameCommand
{
	static
	{
		try
		{
			NMSUtils.register("net.minecraft.server._version_.IChatBaseComponent");
			NMSUtils.register("net.minecraft.server._version_.IChatBaseComponent$ChatSerializer");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutChat");
		}catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public Pause()
	{
		super("pause", "Met la partie en pause.");
	}

	@SuppressWarnings("deprecated")
	public void execute(Player sender, FkPlayer fkp, String[] args) throws ReflectiveOperationException
	{
		if(sender != null)
		{
			if(Fk.getInstance().getGame().getState().equals(Game.GameState.BEFORE_STARTING))
				throw new FkLightException("La partie n'est pas encore commencée.");
			if(Fk.getInstance().getGame().getState().equals(Game.GameState.PAUSE))
				throw new FkLightException("La partie est déjà en pause.");
		}
		Fk.getInstance().getGame().setState(Game.GameState.PAUSE);

		for(World w : Bukkit.getWorlds())
		{
			w.setGameRuleValue("doDaylightCycle", "false");
		}
		Fk.getInstance().getDeepPauseManager().removeAIs();
		Fk.getInstance().getDeepPauseManager().protectDespawnItems();

		if(sender != null)
		{
			int id = Fk.getInstance().getPauseRestorer().registerAll();
			fkp.sendMessage("§b§m-----------");
			fkp.sendMessage("§cCe message n'est visible que par vous.");
			fkp.sendMessage("§aLes inventaires, positions, niveaux d'experience, barre de faim, barre de vie et effets de potions ont été sauvegardés.");

			String message = "{\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/fk game restore " + id + "\"},\"text\":\"Pour tout restaurer, appuyez sur §2§l[Restaurer]\"}";
			PacketUtils.sendJSON(sender, message);

			fkp.sendMessage("§b§m-----------");
			super.broadcast("La partie est maintenant en", "pause", ".");
			Fk.broadcast("§aCeci est la pause n°" + id, FkSound.NOTE_BASS_GUITAR);
		}
	}
}
