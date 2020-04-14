package fr.devsylone.fallenkingdom.players;

import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.scoreboard.FkScoreboard;
import fr.devsylone.fallenkingdom.scoreboard.ScoreboardDisplayer;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;

public class FkPlayer implements fr.devsylone.fkpi.util.Saveable
{
	private boolean knowsSbEdit = false;
	private PlayerState state = PlayerState.INGAME;
	private final String name;
	private FkScoreboard board;
	private ScoreboardDisplayer sbDisplayer;
	private Location portal;

	public enum PlayerState
	{
		INGAME,
		EDITING_SCOREBOARD
	}

	private int kills = 0;
	private int deaths = 0;

	public FkPlayer(String name)
	{
		Fk.getInstance().getPlayerManager().registerNewPlayer(this);
		this.name = name;
		if(Bukkit.getPlayerExact(name) != null)
			board = new FkScoreboard(Bukkit.getPlayer(name));
	}

	public String getName()
	{
		return name;
	}

	public int getKills()
	{
		return kills;
	}

	public int getDeaths()
	{
		return deaths;
	}

	public void addKill()
	{
		kills += 1;
	}

	public void addDeath()
	{
		deaths += 1;
	}

	public void clearDeaths()
	{
		deaths = 0;
	}

	public void clearKills()
	{
		kills = 0;
	}

	public PlayerState getState()
	{
		return state;
	}

	public void setState(PlayerState state)
	{
		this.state = state;
	}

	public void sendMessage(String message)
	{
		sendMessage(message, "", null);
	}

	public void sendMessage(Messages message)
	{
		if (message.getMessage().equals(""))
			return;
		sendMessage(message.getMessage(), "", null);
	}

	public void sendMessage(String message, String prefix)
	{
		sendMessage(message, prefix, null);
	}

	public void sendMessage(String message, String prefix, FkSound sound)
	{
		if(Bukkit.getPlayer(name) != null)
		{
			Player p = Bukkit.getPlayer(name);
			if(sound != null)
			{
				p.playSound(p.getLocation(), sound.bukkitSound(), 1.0F, 1.0F);
			}

			String full = ChatUtils.PREFIX + prefix;

			message = "\n" + message;
			message = message.replaceAll("\\n(?=(§.)*?[^(§.)\\n])", "\n" + full);
			message = message.substring(1);

			p.sendMessage(message);
		}
	}

	public void exitSbDisplayer()
	{
		if(sbDisplayer == null)
			throw new FkLightException("Tu n'es pas en train d'éditer le scoreboard !");

		setState(PlayerState.INGAME);
		sbDisplayer.exit();
		sbDisplayer = null;
	}

	public ScoreboardDisplayer getSbDisplayer()
	{
		if(sbDisplayer == null)
			throw new FkLightException("Tu n'es pas en train d'éditer le scoreboard !");
		return sbDisplayer;
	}

	public void newSbDisplayer()
	{
		if(sbDisplayer != null)
			throw new FkLightException("Tu es déjà en train d'éditer le scoreboard !");

		setState(PlayerState.EDITING_SCOREBOARD);
		sbDisplayer = new ScoreboardDisplayer(this);

		if(Bukkit.getPlayer(name) != null)
			sbDisplayer.display();
	}

	public FkScoreboard getScoreboard()
	{
		return board;
	}

	public void recreateScoreboard()
	{
		if(board != null)
			board.remove();

		board = new FkScoreboard(Bukkit.getPlayer(name));
	}

	public Location getPortal()
	{
		return portal;
	}

	public void setPortal(Location newLoc)
	{
		portal = newLoc;
	}

	public boolean hasAlreadyLearntHowToEditTheBeautifulScoreboard()
	{
		return knowsSbEdit;
	}

	public void knowNowSbEdit()
	{
		knowsSbEdit = true;
	}

	public void load(ConfigurationSection config)
	{
		kills = config.getInt("Kills");
		deaths = config.getInt("Deaths");
		state = PlayerState.valueOf(config.getString("State"));
		knowsSbEdit = config.getBoolean("KnowsSbEdit");

		if(state == PlayerState.EDITING_SCOREBOARD)
			newSbDisplayer();

		if(config.isConfigurationSection("Portal"))
			portal = new Location(Bukkit.getWorld(config.getString("Portal.World")), config.getInt("Portal.X"), config.getInt("Portal.Y"), config.getInt("Portal.Z"));
	}

	public void save(ConfigurationSection config)
	{
		config.set("Kills", kills);
		config.set("Deaths", deaths);
		config.set("State", state.name());
		config.set("KnowsSbEdit", knowsSbEdit);

		if(state == PlayerState.EDITING_SCOREBOARD)
		{
			sbDisplayer.exit();
		}
		if(portal != null && portal.getWorld() != null)
		{
			config.set("Portal.World", portal.getWorld().getName());
			config.set("Portal.X", portal.getBlockX());
			config.set("Portal.Y", portal.getBlockY());
			config.set("Portal.Z", portal.getBlockZ());
		}
	}
}
