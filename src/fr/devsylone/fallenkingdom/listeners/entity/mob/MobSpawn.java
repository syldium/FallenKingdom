package fr.devsylone.fallenkingdom.listeners.entity.mob;

import java.util.Random;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fkpi.rules.ChargedCreepers;

public class MobSpawn implements Listener
{
	@EventHandler
	public void spawn(CreatureSpawnEvent e)
	{
		/*
		 * PAUSE
		 */
		e.setCancelled(Fk.getInstance().getGame().getState().equals(GameState.PAUSE));

		/*
		 * CREEPER
		 */
		
		if(e.getEntityType().equals(EntityType.CREEPER) && (new Random().nextInt(100) < (int)((ChargedCreepers) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("ChargedCreepers")).getSpawn()))
				((Creeper) e.getEntity()).setPowered(true);
	}
}
