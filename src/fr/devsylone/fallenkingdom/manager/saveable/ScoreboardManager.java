package fr.devsylone.fallenkingdom.manager.saveable;

import fr.devsylone.fallenkingdom.utils.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class ScoreboardManager implements Saveable
{
	private String name,
			stringTrue,
			stringFalse,
			noTeam,
			noBase,
			noInfo = "§4?",
			arrows;
	private List<String> sidebar = new ArrayList<>();
	private final List<List<String>> revisions = new ArrayList<>();

	public ScoreboardManager()
	{
		reset();
	}

	public Map<String, String> getCustomStrings()
	{
		Map<String, String> hash = new HashMap<>();
		hash.put("stringTrue", stringTrue);
		hash.put("stringFalse", stringFalse);
		hash.put("noTeam", noTeam);
		hash.put("noBase", noBase);
		hash.put("noInfo", noBase);
		hash.put("arrows", arrows);

		return hash;
	}

	public void setName(String name)
	{
		this.name = name.replaceAll("&", "§");

		recreateAllScoreboards();
	}

	public boolean setLine(int line, String newl)
	{
		if (line < 0 || line >= sidebar.size())
			return false;
		if(newl.length() < 3)
			newl = randomFakeEmpty() + newl;
		line = 15 - line;
		createSnapshot();
		for(int i = sidebar.size(); i <= line; i++)
			sidebar.add(randomFakeEmpty());
		sidebar.set(line, newl);
		recreateAllScoreboards();
		return true;
	}

	public boolean removeLine(int line)
	{
		line = 15 - line;
		createSnapshot();
		if (line < 0 || line >= sidebar.size())
			return false;
		sidebar.remove(line);
		recreateAllScoreboards();
		return true;
	}

	public boolean undo()
	{
		if (revisions.size() < 1)
			return false;
		sidebar = revisions.remove(revisions.size() - 1);
		recreateAllScoreboards();
		return true;
	}

	public void createSnapshot()
	{
		revisions.add(new ArrayList<>(sidebar));
		if (revisions.size() > 5)
			revisions.remove(0);
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

	public String getNoInfo()
	{
		return noInfo;
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
		Scoreboard scoreboard = FkPI.getInstance().getTeamManager().getScoreboard();
		for(Team team : FkPI.getInstance().getTeamManager().getTeams())
		{
			if(Version.VersionType.V1_13.isHigherOrEqual())
				team.getScoreboardTeam().setColor(team.getChatColor());
			else
				team.getScoreboardTeam().setPrefix(String.valueOf(team.getChatColor()));

			for(String entry : team.getPlayers())
			{
				Player player = Bukkit.getPlayer(entry);
				team.getScoreboardTeam().removeEntry(entry);
				if (player != null && Fk.getInstance().getWorldManager().isAffected(player.getWorld()))
					team.getScoreboardTeam().addEntry(entry);
			}
		}
		for(FkPlayer player : Fk.getInstance().getPlayerManager().getConnectedPlayers())
			Objects.requireNonNull(Bukkit.getPlayer(player.getName()), "Player is offline.").setScoreboard(scoreboard);
	}

	public void reset()
	{
		name = ChatUtils.PREFIX;
		stringTrue = "§2✔";
		stringFalse = "§4✘";
		noTeam = "§4No team";
		noBase = "§4No Base";
		noInfo = "§4?";
		arrows = Version.VersionType.V1_13.isHigherOrEqual() ? "⇑⇗⇒⇘⇓⇙⇐⇖" : "↑↗→↘↓↙←↖";
		sidebar.clear();
		sidebar.add("§f§a§k§e");
		sidebar.add("§6Jour {D} §2{H}h{M}");
		sidebar.add("§m------------");
		sidebar.add("Équipe : §e{TEAM}");
		sidebar.add("Morts : §e{MORTS}");
		sidebar.add("Kills : §e{KILLS}");
		sidebar.add("{BASE_PORTAL} : §e{DIST} §l{ARROW}");
		sidebar.add("Pvp {PVP?}");
		sidebar.add("Assauts {TNT?}");
		sidebar.add("Nether {NETHER?}");
		sidebar.add("End {END?}");
		sidebar.add("§m------------");
		sidebar.add(ChatUtils.DEVSYLONE);
	}

	@Override
	public void load(ConfigurationSection config)
	{
		if(!config.contains("Name"))
			return;
		name = config.getString("Name", name);
		sidebar = config.getStringList("Sidebar");

		stringTrue = config.getString("Boolean", "§2✔:§4✘").split(":")[0];
		stringFalse = config.getString("Boolean", "§2✔:§4✘").split(":")[1];
		noTeam = config.getString("NoTeam", noTeam);
		noBase = config.getString("NoBase", noBase);
		noInfo = config.getString("NoInfo", noInfo);
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("Name", name);
		config.set("Sidebar", sidebar);
		config.set("Boolean", stringTrue + ":" + stringFalse);
		config.set("NoTeam", noTeam);
		config.set("NoBase", noBase);
		config.set("NoInfo", noInfo);
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
		StringBuilder rdms = new StringBuilder();
		Random rdm = new Random();
		for(int i = 0; i < 3; i++)
			rdms.append("§").append((char) (rdm.nextInt(26) + 97));

		return rdms.toString();
	}
}
