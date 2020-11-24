package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.teams.teamscommands.TeamTeleport;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener
{
    @EventHandler
    public void respawn(final PlayerRespawnEvent e)
    {
        if(!Fk.getInstance().getWorldManager().isAffected(e.getPlayer().getWorld()))
            return;
        final Team team = Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getPlayer());

        if(FkPI.getInstance().getRulesManager().getRule(Rule.DEATH_LIMIT) > 0)
            if(Fk.getInstance().getPlayerManager().getPlayer(e.getPlayer()).getDeaths() >= FkPI.getInstance().getRulesManager().getRule(Rule.DEATH_LIMIT))
                return;

        if(!isBedOrAnchorSpawn(e) && FkPI.getInstance().getRulesManager().getRule(Rule.RESPAWN_AT_HOME) && team != null && team.getBase() != null)
            Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> TeamTeleport.teleport(team.getBase(), e.getPlayer()), 10L);
    }

    private boolean isBedOrAnchorSpawn(PlayerRespawnEvent event)
    {
        return event.isBedSpawn() || Version.VersionType.V1_16.isHigherOrEqual() && event.isAnchorSpawn();
    }
}
