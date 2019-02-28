package fr.devsylone.fallenkingdom.manager.saveable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.util.Saveable;

public class PlayerManager implements Saveable
{
	private HashMap<String, Location> onTnt;

	private List<FkPlayer> players;

	public PlayerManager()
	{
		onTnt = new HashMap<String, Location>();

		players = new ArrayList<FkPlayer>();
	}

	public List<FkPlayer> getConnectedPlayers()
	{
		List<FkPlayer> list = new ArrayList<FkPlayer>();
		for(FkPlayer p : players)
			if(Bukkit.getPlayer(p.getName()) != null)
				list.add(p);
		return list;
	}

	public void putOnTnt(String player, Location tnt)
	{
		onTnt.put(player, tnt);
	}

	public boolean wasOnTnt(String player)
	{
		return onTnt.containsKey(player);
	}

	public void removeOnTnt(String player)
	{
		onTnt.remove(player);
	}

	public Location getTntLoc(String player)
	{
		if(wasOnTnt(player))
			return onTnt.get(player);
		else
			return null;
	}

	public void registerNewPlayer(FkPlayer p)
	{
		if(!players.contains(p))
			players.add(p);
	}

	public FkPlayer getPlayer(String name)
	{
		for(FkPlayer player : players)
			if(player.getName().equalsIgnoreCase(name))
				return player;

		return new FkPlayer(name);
	}

	public FkPlayer getPlayer(Player p)
	{
		return getPlayer(p.getName());
	}

	@Override
	public void load(ConfigurationSection config)
	{
		if(config.contains("Players"))
			for(String key : config.getConfigurationSection("Players").getKeys(false))
				getPlayer(key).load(config.getConfigurationSection("Players." + key));
	}

	@Override
	public void save(ConfigurationSection config)
	{
		for(FkPlayer p : players)
			p.save(config.createSection("Players." + p.getName()));
	}
}
