package fr.devsylone.fallenkingdom.manager.saveable;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager implements Saveable
{
	private final Map<UUID, Location> onTnt = new HashMap<>();
	private final Map<UUID, FkPlayer> playersByUUID = new HashMap<>();
	private final List<FkPlayer> players = new ArrayList<>();

	public List<FkPlayer> getConnectedPlayers()
	{
		return players.stream()
				.filter(player -> {
					Player p = Bukkit.getPlayer(player.getName());
					return p != null && Fk.getInstance().getWorldManager().isAffected(p.getWorld());
				})
				.collect(Collectors.toList());
	}

	public void putOnTnt(UUID player, Location tnt)
	{
		onTnt.put(player, tnt);
	}

	public boolean wasOnTnt(UUID player)
	{
		return onTnt.containsKey(player);
	}

	public void removeOnTnt(UUID player)
	{
		onTnt.remove(player);
	}

	public Location getTntLoc(UUID player)
	{
		return onTnt.get(player);
	}

	public FkPlayer getPlayer(String name)
	{
		for(FkPlayer player : players)
			if(player.getName().equalsIgnoreCase(name))
				return player;

		FkPlayer player = new FkPlayer(name);
		players.add(player);
		return player;
	}

	public FkPlayer getPlayer(Player player)
	{
		return playersByUUID.computeIfAbsent(player.getUniqueId(), s -> getPlayer(player.getName()));
	}

	public FkPlayer getPlayerIfExist(Player player)
	{
		return playersByUUID.get(player.getUniqueId());
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
