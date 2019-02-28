package fr.devsylone.fallenkingdom.manager.saveable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.util.Saveable;

public class ScoreboardManager implements Saveable
{
	private String name,
			stringTrue,
			stringFalse,
			noTeam,
			noBase,
			arrows;
	private List<String> sidebar;

	public ScoreboardManager()
	{
		name = ChatUtils.PREFIX;
		stringTrue = "§2✔";
		stringFalse = "§4✘";
		noTeam = "§4No team";
		noBase = "§4No Base";
		arrows = "↑↗→↘↓↙←↖";
		sidebar = new ArrayList<String>();
		sidebar.add("§f§a§k§e");
		sidebar.add("§6Jour {D} §2{H}h{M}");
		sidebar.add("§m------------");
		sidebar.add("Equipe : §e{TEAM}");
		sidebar.add("Morts : §e{MORTS}");
		sidebar.add("Kills : §e{KILLS}");
		sidebar.add("Base : §e{DIST} §l{ARROW}");
		sidebar.add("Pvp {PVP?}");
		sidebar.add("Assauts {TNT?}");
		sidebar.add("Nether {NETHER?}");
		sidebar.add("End {END?}");
		sidebar.add("§m------------");
		sidebar.add(ChatUtils.DEVSYLONE);

	}

	public HashMap<String, String> getCustomStrings()
	{
		HashMap<String, String> hash = new HashMap<String, String>();
		hash.put("stringTrue", stringTrue);
		hash.put("stringFalse", stringFalse);
		hash.put("noTeam", noTeam);
		hash.put("noBase", noBase);
		hash.put("arrows", arrows);

		return hash;
	}

	public void setName(String name)
	{
		this.name = name.replaceAll("&", "§");

		for(FkPlayer player : Fk.getInstance().getPlayerManager().getConnectedPlayers())
			player.recreateScoreboard();
	}

	public void setLine(int line, String newl)
	{
		if(newl.matches("(§\\w)+"))
			newl = randomFakeEmpty();
		else if(newl.length() < 5)
			newl += randomFakeEmpty();

		line = 15 - line;
		List<String> newSidebar = new ArrayList<String>();
		for(int i = 0; i < Math.max(line + 1, sidebar.size()); i++)
		{
			if(i == line)
				newSidebar.add(newl);

			else if(sidebar.size() <= i)
				newSidebar.add(randomFakeEmpty());
			else
				newSidebar.add(sidebar.get(i));
		}
		sidebar = newSidebar;
		recreateAllScoreboards();
	}

	public String getName()
	{
		return name;
	}

	public List<String> getSidebar()
	{
		return sidebar;
	}

	public String getTrue()
	{
		return stringTrue;
	}

	public String getFalse()
	{
		return stringFalse;
	}

	public String getNoTeam()
	{
		return noTeam;
	}

	public String getNoBase()
	{
		return noBase;
	}

	public String getArrows()
	{
		return arrows;
	}

	public void recreateAllScoreboards()
	{
		for(FkPlayer player : Fk.getInstance().getPlayerManager().getConnectedPlayers())
			player.recreateScoreboard();
	}

	public void refreshAllScoreboards(PlaceHolder... placeHolders)
	{
		for(FkPlayer player : Fk.getInstance().getPlayerManager().getConnectedPlayers())
			try
			{
				player.getScoreboard().refresh(placeHolders);
			}catch(NullPointerException npe)
			{
				Fk.getInstance().getLogger().warning("Scoreboard null, recreated");
				player.recreateScoreboard();
			}
	}

	public void refreshNicks()
	{
		for(FkPlayer player : Fk.getInstance().getPlayerManager().getConnectedPlayers())
			player.getScoreboard().refreshNicks();
	}

	@Override
	public void load(ConfigurationSection config)
	{
		if(!config.contains("Name"))
			return;
		name = config.getString("Name");
		sidebar = config.getStringList("Sidebar");

		stringTrue = config.getString("Boolean").split(":")[0];
		stringFalse = config.getString("Boolean").split(":")[1];
		noTeam = config.getString("NoTeam");

		noBase = config.getString("NoBase");
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("Name", name);
		config.set("Sidebar", sidebar);
		config.set("Boolean", stringTrue + ":" + stringFalse);
		config.set("NoTeam", noTeam);
		config.set("NoBase", noBase);
		config.set("Arrows", arrows);
	}

	@Override
	public String toString()
	{
		return "name: " + this.name;
	}

	public void removeAllScoreboards()
	{
		for(FkPlayer p : Fk.getInstance().getPlayerManager().getConnectedPlayers())
		{
			if(p != null && p.getScoreboard() != null) // http://fkdevsylone.000webhostapp.com/FK/manage/viewissue.php?id=120
				p.getScoreboard().remove();
		}
	}

	public static String randomFakeEmpty()
	{
		String rdms = "";
		Random rdm = new Random();
		for(int i = 0; i < 3; i++)
			rdms += "§" + (char) (rdm.nextInt(26) + 97);

		return rdms;
	}
}
