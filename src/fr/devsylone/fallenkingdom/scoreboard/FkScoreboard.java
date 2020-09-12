package fr.devsylone.fallenkingdom.scoreboard;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.manager.saveable.ScoreboardManager;
import fr.devsylone.fallenkingdom.players.FkPlayer.PlayerState;
import fr.devsylone.fallenkingdom.utils.RulesFormatter;

public class FkScoreboard
{
	private final Scoreboard bukkitBoard;
	private final FastBoard sidebarBoard;

	private final WeakReference<Player> player;

	private final Map<PlaceHolder, List<Integer>> placeHolders;

	private boolean formatted;

	public FkScoreboard(Player player)
	{
		formatted = true;
		this.player = new WeakReference<>(player);
		this.placeHolders = new HashMap<>();
		this.bukkitBoard = FkPI.getInstance().getTeamManager().getScoreboard();

		sidebarBoard = new FastBoard(player);
		sidebarBoard.updateTitle(Fk.getInstance().getScoreboardManager().getName());

		if(FkPI.getInstance().getRulesManager().getRule(Rule.HEALTH_BELOW_NAME) && bukkitBoard.getObjective("§c❤") == null)
			bukkitBoard.registerNewObjective("§c❤", "health").setDisplaySlot(DisplaySlot.BELOW_NAME);

		List<String> sidebarConfig = Fk.getInstance().getScoreboardManager().getSidebar();

		for(PlaceHolder placeHolder : PlaceHolder.values())
		{
			placeHolders.put(placeHolder, new ArrayList<>());
			for(int i = 0; i < sidebarConfig.size(); i++)
				if(placeHolder.isInLine(sidebarConfig.get(i)))
					placeHolders.get(placeHolder).add(i);
		}

		player.setScoreboard(bukkitBoard);
		refreshAll();
	}

	public void refreshAll()
	{
		Player player = this.player.get();
		if(player == null)
			return;

		if(Fk.getInstance().getGame().getState().equals(GameState.BEFORE_STARTING) && !Fk.getInstance().getPlayerManager().getPlayer(player).getState().equals(PlayerState.EDITING_SCOREBOARD))
		{
			List<String> lines = new ArrayList<>();
			lines.add(Messages.SCOREBOARD_TEAMS.getMessage());

			FkPI.getInstance().getTeamManager().getTeams().stream()
					.limit(10)
					.forEach(team -> lines.add(" " + team.toString() + " (§7" + team.getPlayers().size() + team.getChatColor() + ")"));

			lines.add("§1");
			lines.add(Messages.SCOREBOARD_RULES.getMessage());

			RulesFormatter.formatRules(Rule.ALLOWED_BLOCKS, Rule.CHARGED_CREEPERS, Rule.DISABLED_POTIONS).stream()
					.limit(14 - lines.size())
					.forEach(rule -> lines.add(" " + rule));
			lines.add(" §6... (§e/fk rules list§6)");
			sidebarBoard.updateLines(Version.VersionType.V1_13.isHigherOrEqual() ? lines : lines.stream().map(line -> line.substring(0, Math.min(30, line.length()))).collect(Collectors.toList()));
		}
		else
		{
			for(int i = 0; i < Fk.getInstance().getScoreboardManager().getSidebar().size(); i++)
				refreshLine(i);
		}
		Fk.getInstance().getScoreboardManager().refreshNicks();

		try
		{
			player.setScoreboard(bukkitBoard);
		}catch(IllegalStateException ignored)
		{
			// Pas de player connection
		}
	}

	public void refresh(PlaceHolder... placeHolders)
	{
		Player player = this.player.get();
		if(player == null)
			return;

		if(placeHolders.length == 0)
		{
			refreshAll();
			return;
		}
		if(Fk.getInstance().getGame().getState() == GameState.BEFORE_STARTING && !Fk.getInstance().getPlayerManager().getPlayer(player).getState().equals(PlayerState.EDITING_SCOREBOARD))
			return;

		List<Integer> linesToRefresh = new ArrayList<>();

		for(PlaceHolder p : placeHolders)
		{
			for(Integer i : this.placeHolders.get(p))
				if(!linesToRefresh.contains(i))
					linesToRefresh.add(i);
		}

		for(Integer i : linesToRefresh)
			refreshLine(i);
	}

	public void setFormatted(boolean bool)
	{
		formatted = bool;

		for(int i = 0; i < Fk.getInstance().getScoreboardManager().getSidebar().size(); i++)
			refreshLine(i);
	}

	private void refreshLine(int i)
	{
		Player player = this.player.get();
		if(player == null || !Fk.getInstance().getWorldManager().isAffected(player.getWorld()))
			return;

		if(Fk.getInstance().getGame().getState() == GameState.BEFORE_STARTING && !Fk.getInstance().getPlayerManager().getPlayer(player).getState().equals(PlayerState.EDITING_SCOREBOARD))
			return;

		String line = Fk.getInstance().getScoreboardManager().getSidebar().get(i);
		if(formatted)
		{
			for(PlaceHolder placeHolder : placeHolders.keySet())
				if(placeHolders.get(placeHolder).contains(i))
					line = placeHolder.replace(line, player, placeHolders.get(placeHolder).indexOf(i));
		}
		else
		{
			line = line.replaceAll("§", "&");
			line = line.replaceAll("(&.)+$", ScoreboardManager.randomFakeEmpty());
		}

		if(i >= sidebarBoard.getLines().size() || !sidebarBoard.getLine(i).equals(line))
		{
			if(!Version.VersionType.V1_13.isHigherOrEqual())
				line = line.substring(0, Math.min(30, line.length()));
			sidebarBoard.updateLine(i, line);
		}
	}

	public void remove()
	{
		if(!sidebarBoard.isDeleted())
			sidebarBoard.delete();
	}
}