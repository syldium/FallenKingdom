package fr.devsylone.fallenkingdom.listeners.entity.player;

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
		if(Fk.getInstance().getGame().getState().equals(GameState.PAUSE) && (Boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DeepPause").getValue())
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
		
		if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(((Player)e.getEntity()).getName()) == null)
			return;

		else if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(damager.getName()) != null && Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(((Player)e.getEntity()).getName()).equals(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(damager.getName())) && !(Boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("FriendlyFire").getValue())
			e.setCancelled(true);

		else if(!Fk.getInstance().getGame().isPvpEnabled())
			e.setCancelled(true);

	}
}
