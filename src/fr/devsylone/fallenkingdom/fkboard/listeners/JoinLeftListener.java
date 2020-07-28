package fr.devsylone.fallenkingdom.fkboard.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.fkboard.websocket.responses.PlayerChange;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.ITeam;

public class JoinLeftListener implements Listener {

    private final Fk plugin;

    public JoinLeftListener(Fk plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getPlayerStatus().update(event.getPlayer().getName(),true);
        broadcastPlayerChange(event.getPlayer(), true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerStatus().update(event.getPlayer().getName(),false);
        broadcastPlayerChange(event.getPlayer(), false);
    }

    private void broadcastPlayerChange(Player player, boolean online) {
        ITeam team = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
        PlayerChange playerChange = new PlayerChange(player.getName(), team, online);
        plugin.getOptionalFkBoardWebSocket().ifPresent(fkws -> fkws.send(playerChange.toJSON()));
    }
}
