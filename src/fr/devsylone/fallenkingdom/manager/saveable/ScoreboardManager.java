package fr.devsylone.fallenkingdom.manager.saveable;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @deprecated {@link fr.devsylone.fallenkingdom.display.GlobalDisplayService}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public class ScoreboardManager implements Saveable
{
	public String format(boolean value) {
		return Fk.getInstance().getDisplayService().text().format(value);
	}

	public Set<Integer> getLinesWith(PlaceHolder... placeHolders)
	{
		return Collections.emptySet();
	}

	public void setName(String name)
	{
		Fk.getInstance().getDisplayService().setScoreboardTitle(ChatColor.translateAlternateColorCodes('&', name));
		recreateAllScoreboards();
	}

	public boolean setLine(int line, String newl)
	{
		boolean edited = Fk.getInstance().getDisplayService().setScoreboardLine(Fk.getInstance().getDisplayService().scoreboard().reverseIndex(line), newl);
		recreateAllScoreboards();
		return edited;
	}

	public boolean removeLine(int line)
	{
		return setLine(line, null);
	}

	public boolean undo()
	{
		return Fk.getInstance().getDisplayService().undo();
	}

	public void createSnapshot()
	{
		// noop
	}

	public String getName()
	{
		return Fk.getInstance().getDisplayService().scoreboard().title();
	}

	public List<String> getSidebar()
	{
		return Fk.getInstance().getDisplayService().scoreboard().lines();
	}

	public void setSidebar(List<String> sidebar, boolean addDevsyloneText)
	{
		Fk.getInstance().getDisplayService().setScoreboardLines(sidebar);
	}

	public boolean isDefaultSidebar()
	{
		return Fk.getInstance().getDisplayService().scoreboard().isDefaultSidebar();
	}

	public String getSidebarLine(int index, Player player)
	{
		return Fk.getInstance().getDisplayService().scoreboard().renderLine(player, Fk.getInstance().getPlayerManager().getPlayer(player), index);
	}

	public String getTrue()
	{
		return format(true);
	}

	public String getFalse()
	{
		return format(false);
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
		return Fk.getInstance().getDisplayService().text().noInfo();
	}

	public String getArrows()
	{
		return Fk.getInstance().getDisplayService().text().arrows();
	}

	public void recreateAllScoreboards()
	{
		Fk.getInstance().getDisplayService().updateAll();
	}

	public void refreshAllScoreboards(PlaceHolder... placeHolders)
	{
		Fk.getInstance().getDisplayService().updateAll(placeHolders);
		refreshNicks();
	}

	public void refreshNicks()
	{
		// noop
	}

	public void reset()
	{
		// noop
	}

	@Override
	public void load(ConfigurationSection config)
	{
		// noop
	}

	@Override
	public void save(ConfigurationSection config)
	{
		// noop
	}

	public void removeAllScoreboards()
	{
		Fk.getInstance().getDisplayService().hideAll();
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
