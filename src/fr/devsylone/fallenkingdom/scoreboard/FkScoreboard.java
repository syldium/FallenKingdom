package fr.devsylone.fallenkingdom.scoreboard;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.display.GlobalDisplayService;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.players.FkPlayer.PlayerState;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.RulesFormatter;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FkScoreboard
{

	private final GlobalDisplayService displayService;
	private final FastBoard sidebarBoard;

	private final FkPlayer fkPlayer;
	private final WeakReference<Player> player;

	public FkScoreboard(@NotNull FkPlayer fkPlayer, @NotNull Player player)
	{
		this.fkPlayer = fkPlayer;
		this.player = new WeakReference<>(player);
		this.displayService = fkPlayer.getDisplayService();

		this.sidebarBoard = new FastBoard(player);
		this.sidebarBoard.updateTitle(this.displayService.scoreboard().title());

		refreshAll();
	}

	public void updateLines(@NotNull Collection<@NotNull String> lines)
	{
		if (Version.VersionType.V1_13.isHigherOrEqual()) {
			List<String> scores = null;
			if (this.fkPlayer.getState() == PlayerState.EDITING_SCOREBOARD) {
				scores = new ArrayList<>(lines.size());
				for (int line = lines.size() - 1; line >= 0; line--) {
					scores.add(ChatColor.RED + String.valueOf(line));
				}
			}
			this.sidebarBoard.updateLines(lines, scores);
		} else {
			final List<String> truncated = new ArrayList<>(lines.size());
			for (String line : lines) {
				truncated.add(line.substring(0, Math.min(30, line.length())));
			}
			this.sidebarBoard.updateLines(truncated);
		}
	}

	public void updateLine(int line, @NotNull String text)
	{
		if (Version.VersionType.V1_13.isHigherOrEqual()) {
			String score = null;
			if (this.fkPlayer.getState() == PlayerState.EDITING_SCOREBOARD) {
				score = ChatColor.RED + String.valueOf(this.displayService.scoreboard().reverseIndex(line));
			}
			this.sidebarBoard.updateLine(line, text, score);
		} else {
			this.sidebarBoard.updateLine(line, text.substring(0, Math.min(30, text.length())));
		}
	}

	public void refreshAll()
	{
		Player player = this.player.get();
		if(player == null)
			return;

		if(Fk.getInstance().getGame().isPreStart() && !Fk.getInstance().getPlayerManager().getPlayer(player).getState().equals(PlayerState.EDITING_SCOREBOARD))
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
			this.updateLines(lines);
		}
		else
		{
			this.updateLines(displayService.scoreboard().renderLines(player, this.fkPlayer));
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
		if(Fk.getInstance().getGame().isPreStart() && this.fkPlayer.getState() != PlayerState.EDITING_SCOREBOARD)
			return;

		this.displayService.update(player, this.fkPlayer, placeHolders);
	}

	public @Nullable Player player()
	{
		return this.player.get();
	}

	/**
	 * @deprecated {@link FkPlayer#setUseFormattedText(boolean)}
	 */
	@Deprecated
	public void setFormatted(boolean formatted)
	{
		this.fkPlayer.setUseFormattedText(formatted);
	}

	public void remove()
	{
		if(!sidebarBoard.isDeleted())
			sidebarBoard.delete();
	}
}