package fr.devsylone.fallenkingdom.pause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.util.Saveable;

import static fr.devsylone.fallenkingdom.utils.ConfigHelper.loadSectionsWithIndex;

public class PauseRestorer implements Saveable
{
	private int lastID = 0;
	private final Map<Integer, List<PausedPlayer>> pauses = new HashMap<>();

	public void registerPlayer(int id, Player p)
	{
		registerPlayer(id, new PausedPlayer(p));
	}

	public void registerPlayer(int id, PausedPlayer pp)
	{
		List<PausedPlayer> pausedPlayers = pauses.computeIfAbsent(id, s -> new ArrayList<>());
		pausedPlayers.remove(pp);
		pausedPlayers.add(pp);
	}

	public int registerAll()
	{
		int id = ++lastID;
		for(Player p : Bukkit.getOnlinePlayers())
			registerPlayer(id, p);
		return id;
	}

	public int restoreAll(int id)
	{
		if(id < 0)
			id  = lastID;

		List<PausedPlayer> pausedPlayers = pauses.get(id);
		if(pausedPlayers == null)
			throw new FkLightException(Messages.CMD_GAME_RESTORE_INVALID_ID);

		List<String> failed = new LinkedList<>();
		for(PausedPlayer p : pausedPlayers)
			if(!p.tryRestore())
				failed.add(p.getPlayer());

		if(!failed.isEmpty())
			Fk.broadcast(Messages.CMD_GAME_RESTORE_PLAYERS_DOESNOT_CONNECT + " §b" + String.join("§c, §b", failed));
		return id;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		loadSectionsWithIndex(config, (id, section) -> registerPlayer(id, new PausedPlayer(section)));
	}

	@Override
	public void save(ConfigurationSection config)
	{
		for(Map.Entry<Integer, List<PausedPlayer>> entry : pauses.entrySet())
			for(PausedPlayer p : entry.getValue())
				p.save(config.createSection(entry.getKey() + "." + p.getPlayer()));
	}

}
