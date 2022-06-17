package fr.devsylone.fallenkingdom.listeners;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.display.NametagService;
import fr.devsylone.fkpi.api.event.PlayerTeamChangeEvent;
import fr.devsylone.fkpi.api.event.TeamUpdateEvent;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public class TeamChangeListener implements Listener {

    private final Fk plugin;

    public TeamChangeListener(@NotNull Fk plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeamChange(TeamUpdateEvent event) {
        final NametagService service = this.plugin.getFkPI().getTeamManager().nametag();
        final BukkitScheduler scheduler = this.plugin.getServer().getScheduler();
        final Team team = (Team) event.getTeam();
        switch (event.getUpdateType()) {
            case CREATION:
                service.createScoreboardTeam(team);
                break;
            case DELETION:
                service.removeScoreboardTeam(team);
                break;
            case UPDATE:
                final String previousName = team.getName();
                scheduler.runTask(this.plugin, () -> {
                    if (previousName.equals(team.getName())) {
                        service.updateColor(team);
                    } else {
                        service.renameTeam(team, previousName);
                    }
                });
        }
    }

    @EventHandler
    public void onPlayerTeamChange(PlayerTeamChangeEvent event) {
        final NametagService service = this.plugin.getFkPI().getTeamManager().nametag();
        final Team from = (Team) event.getFrom();
        final Team to = (Team) event.getTeam();
        final Player player = this.plugin.getServer().getPlayer(event.getPlayerName());
        if (from != null) {
            service.removeEntry(from, event.getPlayerName(), player);
        }
        if (to != null) {
            service.addEntry(to, event.getPlayerName(), player);
        }
    }
}
