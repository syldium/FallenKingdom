package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.managers.TeamManager;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

public class NametagService implements Saveable {

    private Scoreboard scoreboard;
    private final TeamManager teamManager;

    public NametagService(@NotNull TeamManager teamManager) {
        final ScoreboardManager manager = Bukkit.getScoreboardManager();
        final Scoreboard mainScoreboard = manager.getMainScoreboard();
        this.scoreboard = mainScoreboard.getTeams().isEmpty() ? mainScoreboard : manager.getNewScoreboard();
        this.teamManager = teamManager;
    }

    public void createHealthObjective() {
        if (this.scoreboard.getObjective("§c❤") == null) {
            this.scoreboard.registerNewObjective("§c❤", "health").setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
    }

    public void removeHealthObjective() {
        final Objective objective = this.scoreboard.getObjective("§c❤");
        if (objective != null) {
            objective.unregister();
        }
    }

    public @NotNull Scoreboard scoreboard() {
        return this.scoreboard;
    }

    public void addEntry(@NotNull Player player) {
        final Team team = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
        if (team != null) {
            player.setDisplayName(team.getChatColor() + player.getName());
            team.getScoreboardTeam().addEntry(player.getName());
            player.setScoreboard(this.scoreboard);
        }
    }

    public void removeEntry(@NotNull Player player) {
        final Team team = this.teamManager.getPlayerTeam(player);
        if (team != null) {
            team.getScoreboardTeam().removeEntry(player.getName());
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    private static final String USE_MAIN_SCOREBOARD = "use-main-scoreboard";

    @Override
    public void load(ConfigurationSection config) {
        final ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (config.getBoolean(USE_MAIN_SCOREBOARD)) {
            this.scoreboard = manager.getMainScoreboard();
        } else {
            this.scoreboard = manager.getNewScoreboard();
        }
    }

    @Override
    public void save(ConfigurationSection config) {
        config.set(USE_MAIN_SCOREBOARD, this.scoreboard == Bukkit.getScoreboardManager().getMainScoreboard());
    }
}
