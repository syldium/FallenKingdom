package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;

public class PvpListener implements Listener
{
	@EventHandler
	public void pvp(EntityDamageByEntityEvent e)
	{
		if(!Fk.getInstance().getWorldManager().isAffected(e.getEntity().getWorld()))
			return;

		if(Fk.getInstance().getGame().getState().equals(GameState.PAUSE) && FkPI.getInstance().getRulesManager().getRule(Rule.DEEP_PAUSE))
		{
			e.setCancelled(true);
			return;
		}

		Player damager = null;
		
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player)
			damager = (Player) e.getDamager();

		else if(e.getEntity() instanceof Player && e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)
			damager = (Player) ((Projectile) e.getDamager()).getShooter();

		else
			return;
		
		if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getEntity().getName()) == null)
			return;

		else if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(damager.getName()) != null && Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getEntity().getName()).equals(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(damager.getName())) && !FkPI.getInstance().getRulesManager().getRule(Rule.FRIENDLY_FIRE))
			e.setCancelled(true);

		else if(!Fk.getInstance().getGame().isPvpEnabled())
			e.setCancelled(true);

	}
}
