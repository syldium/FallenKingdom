package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;

public class SpawnProtection implements Listener
{

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{

		if(Fk.getInstance().getGame().getState() == GameState.BEFORE_STARTING) {
			if (e.getEntity() instanceof Player)
				e.setCancelled(true);
			
		}
	}
		
}
	


