package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.version.Version;
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
import org.jetbrains.annotations.Nullable;

/**
 * Adapte les équipes FK en équipes scoreboard.
 */
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

    /**
     * S'assure que l'équipe a bien été créée du point de vue du scoreboard.
     *
     * @param team L'équipe FK
     */
    public void createScoreboardTeam(@NotNull Team team) {
        getOrCreateScoreboardTeam(team);
    }

    /**
     * Retire l'équipe du scoreboard.
     *
     * @param fkTeam L'équipe FK
     */
    public void removeScoreboardTeam(@NotNull Team fkTeam) {
        org.bukkit.scoreboard.Team team = this.scoreboard.getTeam(fkTeam.getName());
        if (team != null) {
            team.unregister();
        }
    }

    /**
     * Ajoute un joueur à l'équipe dans le scoreboard.
     *
     * @param team L'équipe FK où le joueur est déjà
     * @param playerName Le nom du joueur (identique à {@code player.getName()})
     * @param player Le joueur concerné s'il est connecté
     */
    public void addEntry(@NotNull Team team, @NotNull String playerName, @Nullable Player player) {
        getOrCreateScoreboardTeam(team).addEntry(playerName);
        if (player != null) {
            player.setDisplayName(team.getChatColor() + player.getName());
            player.setScoreboard(this.scoreboard);
        }
    }

    /**
     * Retire un joueur à l'équipe dans le scoreboard.
     *
     * @param team L'équipe FK où le joueur n'est pas
     * @param playerName Le nom du joueur (identique à {@code player.getName()})
     * @param player Le joueur concerné s'il est connecté
     */
    public void removeEntry(@NotNull Team team, @NotNull String playerName, @Nullable Player player) {
        getOrCreateScoreboardTeam(team).removeEntry(playerName);
        if (player != null) {
            player.setDisplayName(playerName);
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    /**
     * Met à jour l'affichage scoreboard d'un joueur.
     * <p>
     * Si le scoreboard doit changer à cause du joueur, utiliser cette méthode.
     * Si le scoreboard doit changer à cause d'un changement de l'équipe, préférer {@link #addEntry(Team, String, Player)}.
     *
     * @param player Le joueur concerné
     */
    public void addEntry(@NotNull Player player) {
        final Team team = this.teamManager.getPlayerTeam(player);
        if (team != null) {
            addEntry(team, player.getName(), player);
        }
    }

    /**
     * Retire l'affichage scoreboard d'un joueur.
     *
     * @param player Le joueur concerné
     */
    public void removeEntry(@NotNull Player player) {
        final Team team = this.teamManager.getPlayerTeam(player);
        if (team != null) {
            removeEntry(team, player.getName(), player);
        }
    }

    /**
     * Prend en compte le changement de nom d'une équipe.
     *
     * @param fkTeam L'équipe FK avec un nouveau nom
     * @param previousName L'ancien nom de l'équipe
     */
    public void renameTeam(@NotNull Team fkTeam, @NotNull String previousName) {
        org.bukkit.scoreboard.Team team = getOrCreateScoreboardTeam(fkTeam);
        for (String entry : fkTeam.getPlayers()) {
            team.addEntry(entry);
        }
        team = this.scoreboard.getTeam(previousName);
        if (team != null) {
            team.unregister();
        }
    }

    /**
     * Prend en compte le changement de couleur d'une équipe.
     *
     * @param fkTeam L'équipe FK avec une nouvelle couleur
     */
    public void updateColor(@NotNull Team fkTeam) {
        setTeamColor(getOrCreateScoreboardTeam(fkTeam), fkTeam);
    }

    public void teardown(@NotNull Iterable<Team> teams) {
        for (Team team : teams) {
            removeScoreboardTeam(team);
        }
        removeHealthObjective();
    }

    private void setTeamColor(org.bukkit.scoreboard.Team team, Team fkTeam) {
        if (Version.VersionType.V1_13.isHigherOrEqual()) {
            team.setColor(fkTeam.getColor().getBukkitChatColor());
        } else {
            team.setPrefix(String.valueOf(fkTeam.getColor().getBukkitChatColor()));
        }
    }

    private @NotNull org.bukkit.scoreboard.Team getOrCreateScoreboardTeam(@NotNull Team fkTeam) {
        org.bukkit.scoreboard.Team team = this.scoreboard.getTeam(fkTeam.getName());
        if (team != null) {
            return team;
        }
        team = this.scoreboard.registerNewTeam(fkTeam.getName());
        setTeamColor(team, fkTeam);
        return team;
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
