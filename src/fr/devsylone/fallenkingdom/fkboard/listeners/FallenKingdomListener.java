package fr.devsylone.fallenkingdom.fkboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.fkboard.websocket.responses.PlayerChange;
import fr.devsylone.fallenkingdom.fkboard.websocket.responses.RuleChange;
import fr.devsylone.fallenkingdom.fkboard.websocket.responses.TeamsList;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.PlayerTeamChangeEvent;
import fr.devsylone.fkpi.api.event.RuleChangeEvent;
import fr.devsylone.fkpi.api.event.TeamUpdateEvent;

public class FallenKingdomListener implements Listener {

    private final Fk plugin;

    public FallenKingdomListener(Fk plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeamChange(PlayerTeamChangeEvent event) {
        PlayerChange playerChange = new PlayerChange(event.getPlayerName(), event.getTeam(), Bukkit.getPlayer(event.getPlayerName()) != null);
        plugin.getOptionalFkBoardWebSocket().ifPresent(fkws -> fkws.send(playerChange.toJSON()));
    }

    @EventHandler
    public void onTeamChange(TeamUpdateEvent event) {
        if (event.getUpdateType().equals(TeamUpdateEvent.TeamUpdate.SET_BASE)) {
            return;
        }
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            TeamsList teamsList = new TeamsList(FkPI.getInstance().getTeamManager().getTeams(), plugin.getPlayerStatus());
            plugin.getOptionalFkBoardWebSocket().ifPresent(fkws -> fkws.send(teamsList.toJSON()));
        }, 1L);
    }

    @EventHandler
    public <T> void onRuleChange(RuleChangeEvent<T> event) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            RuleChange ruleChange = new RuleChange(event.getRule(), event.getValue());
            plugin.getOptionalFkBoardWebSocket().ifPresent(fkws -> fkws.send(ruleChange.toJSON()));
        }, 1L);
    }
}
