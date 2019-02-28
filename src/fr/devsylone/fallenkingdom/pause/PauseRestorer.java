package fr.devsylone.fallenkingdom.pause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fkpi.util.Saveable;

public class PauseRestorer implements Saveable
{
	private int lastID;
	HashMap<Integer, List<PausedPlayer>> pauses;

	public PauseRestorer()
	{
		pauses = new HashMap<Integer, List<PausedPlayer>>();
		lastID = 0;
	}

	public void registerPlayer(int id, Player p)
	{
		registerPlayer(id, new PausedPlayer(p));
	}

	public void registerPlayer(int id, PausedPlayer pp)
	{
		if(!pauses.containsKey(id))
			pauses.put(id, new ArrayList<PausedPlayer>());

		if(pauses.get(id).contains(pp))
			pauses.get(id).remove(pp);

		pauses.get(id).add(pp);
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
		if(id == -1)
			id  = lastID;
		
		if(!pauses.containsKey(id))
			throw new FkLightException("id invalide");
		List<String> failed = new ArrayList<String>();
		for(PausedPlayer p : pauses.get(id))
			if(!p.tryRestore())
				failed.add(p.getPlayer());

		if(!failed.isEmpty())
			Fk.broadcast("§cAttention, les joueurs suivants ne sont pas connectés et leur état n'a pas été réstauré : §b" + String.join("§c, §b", failed));
		return id;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		for(String id : config.getKeys(false))
		{
			for(String pp : config.getConfigurationSection(id).getKeys(false))
				registerPlayer(Integer.parseInt(id), new PausedPlayer(config.getConfigurationSection(id + "." + pp)));
			lastID = Integer.parseInt(id);
		}
	}

	@Override
	public void save(ConfigurationSection config)
	{
		for(int i = 1; i <= lastID; i++)
			for(PausedPlayer p : pauses.get(i))
				p.save(config.createSection(i + "." + p.getPlayer()));
	}

}
