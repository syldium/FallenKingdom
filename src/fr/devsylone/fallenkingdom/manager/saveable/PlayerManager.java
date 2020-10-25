package fr.devsylone.fallenkingdom.manager.saveable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.devsylone.fallenkingdom.Fk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.util.Saveable;

public class PlayerManager implements Saveable
{
	protected final Map<String, Location> onTnt = new HashMap<>();
	protected final List<FkPlayer> players = new ArrayList<>();

	public List<FkPlayer> getConnectedPlayers()
	{
		return players.stream()
				.filter(player -> {
					Player p = Bukkit.getPlayer(player.getName());
					return p != null && Fk.getInstance().getWorldManager().isAffected(p.getWorld());
				})
				.collect(Collectors.toList());
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
		return onTnt.getOrDefault(player, null);
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

	public FkPlayer getPlayerIfExist(String name)
	{
		for(FkPlayer player : players)
			if(player.getName().equalsIgnoreCase(name))
				return player;

		return null;
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
