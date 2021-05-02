package fr.devsylone.fallenkingdom.manager.saveable;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ScoreboardManager implements Saveable
{
	private String name,
			stringTrue,
			stringFalse,
			noInfo = "§4?",
			arrows;
	private List<String> sidebar = new ArrayList<>();
	private final Map<PlaceHolder, List<Integer>> placeHolders = new EnumMap<>(PlaceHolder.class);
	private final List<List<String>> revisions = new ArrayList<>();

	public ScoreboardManager()
	{
		reset();
	}

	public String format(boolean value) {
		return value ? stringTrue : stringFalse;
	}

	public Set<Integer> getLinesWith(PlaceHolder... placeHolders)
	{
		Set<Integer> lines = new HashSet<>();
		for (PlaceHolder placeHolder : placeHolders) {
			lines.addAll(this.placeHolders.get(placeHolder));
		}
		return lines;
	}

	public void setName(String name)
	{
		this.name = ChatColor.translateAlternateColorCodes('&', name);

		recreateAllScoreboards();
	}

	public boolean setLine(int line, String newl)
	{
		if (line < 0 || line >= 15)
			return false;
		line = sidebar.size() - line - 1;
		if(newl.length() < 3)
			newl = randomFakeEmpty() + newl;
		createSnapshot();
		for(int i = line; i < 0; i++)
			sidebar.add(0, randomFakeEmpty());
		if(line < 0)
			line = 0;
		sidebar.set(line, newl);
		computePlaceHoldersIndexes();
		recreateAllScoreboards();
		return true;
	}

	public boolean removeLine(int line)
	{
		if (line < 0 || line >= 15)
			return false;
		line = sidebar.size() - line - 1;
		createSnapshot();
		sidebar.remove(line);
		computePlaceHoldersIndexes();
		recreateAllScoreboards();
		return true;
	}

	public boolean undo()
	{
		if (revisions.size() < 1)
			return false;
		sidebar = revisions.remove(revisions.size() - 1);
		computePlaceHoldersIndexes();
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

	public void setSidebar(List<String> sidebar, boolean addDevsyloneText)
	{
		this.sidebar = new ArrayList<>(sidebar);
		if (addDevsyloneText) {
			this.sidebar.add(ChatUtils.DEVSYLONE);
		}
		computePlaceHoldersIndexes();
	}

	public boolean isDefaultSidebar()
	{
		String[] def = Messages.SCOREBOARD_DEFAULT.getMessage().split("\n");
		if (def.length != sidebar.size() && def.length != sidebar.size() - 1) {
			return false;
		}

		for (int i = 0; i < def.length; i++) {
			if (!def[i].equals(sidebar.get(i))) {
				return false;
			}
		}
		return true;
	}

	public String getSidebarLine(int index, Player player)
	{
		String line = sidebar.get(index);
		if (player == null) {
			char[] b = line.toCharArray();
			boolean atEnd = true;
			for (int i = b.length - 1; i >= 0; i--) {
				if (b[i] == ChatColor.COLOR_CHAR) {
					if ((b.length - i) % 2 != 0) {
						atEnd = false;
					}
					if (!atEnd) {
						b[i] = '&';
					}
				} else if (atEnd && (b.length - i) % 2 == 0) {
					atEnd = false;
				}
			}
			return new String(b);
		}

		for (PlaceHolder placeHolder : PlaceHolder.values()) {
			List<Integer> lines = placeHolders.get(placeHolder);
			if (lines == null) {
				continue;
			}

			int position = lines.indexOf(index);
			if (position > -1) {
				line = placeHolder.replace(line, player, position);
			}
		}
		return line;
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
		return Messages.CMD_SCOREBOARD_NO_TEAM.getMessage();
	}

	public String getNoBase()
	{
		return Messages.CMD_SCOREBOARD_NO_BASE.getMessage();
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
		refreshNicks();
	}

	public void refreshNicks()
	{
		Scoreboard scoreboard = FkPI.getInstance().getTeamManager().getScoreboard();
		for(Team team : FkPI.getInstance().getTeamManager().getTeams())
		{
			if(Version.VersionType.V1_13.isHigherOrEqual())
				team.getScoreboardTeam().setColor(team.getColor().getBukkitChatColor()); // À quand des couleurs de team RGB ?
			else
				team.getScoreboardTeam().setPrefix(team.getChatColor().toString());

			for(String entry : team.getScoreboardTeam().getEntries())
			{
				if(!team.getPlayers().contains(entry))
				{
					team.getScoreboardTeam().removeEntry(entry);
					Player player = Bukkit.getPlayer(entry);
					if(player != null)
					{
						player.setDisplayName(player.getName());
						//player.setPlayerListName(player.getName());
					}
				}
			}
			for(String entry : team.getPlayers())
			{
				Player player = Bukkit.getPlayer(entry);
				if(player != null && Fk.getInstance().getWorldManager().isAffected(player.getWorld()))
				{
					team.getScoreboardTeam().addEntry(entry);
					player.setDisplayName(team.getChatColor() + player.getName());
					//player.setPlayerListName(team.getChatColor() + player.getName()); // S'affiche dans le tab mais pas au dessus du joueur - très perturbant
				}
				else if(team.getScoreboardTeam().getEntries().contains(entry))
					team.getScoreboardTeam().removeEntry(entry);
			}
		}
		for(Player player : Fk.getInstance().getPlayerManager().getOnlinePlayers())
			player.setScoreboard(scoreboard);
	}

	public void reset()
	{
		name = ChatUtils.PREFIX;
		stringTrue = "§2✔";
		stringFalse = "§4✘";
		noInfo = "§4?";
		arrows = Version.VersionType.V1_13.isHigherOrEqual() ? "⇑⇗⇒⇘⇓⇙⇐⇖" : "↑↗→↘↓↙←↖";
		sidebar.clear();
		sidebar.addAll(Arrays.asList(Messages.SCOREBOARD_DEFAULT.getMessage().split("\n")));
		sidebar.add(ChatUtils.DEVSYLONE);
		computePlaceHoldersIndexes();
	}

	@Override
	public void load(ConfigurationSection config)
	{
		if(!config.contains("Name"))
			return;
		name = config.getString("Name", name);
		sidebar = config.getStringList("Sidebar");
		computePlaceHoldersIndexes();

		stringTrue = config.getString("Boolean", "§2✔:§4✘").split(":")[0];
		stringFalse = config.getString("Boolean", "§2✔:§4✘").split(":")[1];
		noInfo = config.getString("NoInfo", noInfo);
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("Name", name);
		config.set("Sidebar", sidebar);
		config.set("Boolean", stringTrue + ":" + stringFalse);
		config.set("NoInfo", noInfo);
		config.set("Arrows", arrows);
	}

	@Override
	public String toString() {
		return "ScoreboardManager{" +
				"name='" + name + '\'' +
				",\n sidebar=" + sidebar +
				",\n placeHolders=" + placeHolders +
				'}';
	}

	public void removeAllScoreboards()
	{
		for(FkPlayer p : Fk.getInstance().getPlayerManager().getConnectedPlayers())
		{
			if(p.getScoreboard() != null) // http://fkdevsylone.000webhostapp.com/FK/manage/viewissue.php?id=120
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

	private void computePlaceHoldersIndexes()
	{
		for (PlaceHolder placeHolder : PlaceHolder.values()) {
			placeHolders.put(placeHolder, new ArrayList<>(1));
			for (int i = 0; i < sidebar.size(); i++) {
				if (placeHolder.isInLine(sidebar.get(i))) {
					placeHolders.get(placeHolder).add(i);
				}
			}
		}
	}
}
