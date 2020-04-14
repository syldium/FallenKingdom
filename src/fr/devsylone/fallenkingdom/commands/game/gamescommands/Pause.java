package fr.devsylone.fallenkingdom.commands.game.gamescommands;

import fr.devsylone.fallenkingdom.utils.Messages;
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
		super("pause", Messages.CMD_MAP_GAME_PAUSE.getMessage());
		permission = ADMIN_PERMISSION;
	}

	@SuppressWarnings("deprecated")
	public void execute(Player sender, FkPlayer fkp, String[] args)
	{
		if(sender != null)
		{
			if(Fk.getInstance().getGame().getState().equals(Game.GameState.BEFORE_STARTING))
				throw new FkLightException(Messages.CMD_ERROR_GAME_NOT_STARTED);
			if(Fk.getInstance().getGame().getState().equals(Game.GameState.PAUSE))
				throw new FkLightException(Messages.CMD_ERROR_ALREADY_IN_PAUSE);
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
			fkp.sendMessage(Messages.CMD_GAME_PAUSE_SAVE_INFO);

			String message = "{\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/fk game restore " + id + "\"},\"text\":\"" + Messages.CMD_GAME_PAUSE_RESTORE_INVITE.getMessage() + "\"}";
			PacketUtils.sendJSON(sender, message);

			fkp.sendMessage("§b§m-----------");
			super.broadcast(Messages.CMD_GAME_PAUSE.getMessage());
			Fk.broadcast(Messages.CMD_GAME_PAUSE_ID.getMessage().replace("%id%", String.valueOf(id)), FkSound.NOTE_BASS_GUITAR);
		}
	}
}
