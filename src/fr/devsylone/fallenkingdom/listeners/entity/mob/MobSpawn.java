package fr.devsylone.fallenkingdom.listeners.entity.mob;

import java.util.Random;

import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import fr.devsylone.fallenkingdom.Fk;

public class MobSpawn implements Listener
{
	@EventHandler
	public void spawn(CreatureSpawnEvent e)
	{
		if (!Fk.getInstance().getWorldManager().isAffected(e.getEntity().getWorld()))
			return;

		if (Fk.getInstance().getGame().isPaused())
			e.setCancelled(true);

		/*
		 * CREEPER
		 */
		
		if(e.getEntityType().equals(EntityType.CREEPER) && (new Random().nextInt(100) < FkPI.getInstance().getRulesManager().getRule(Rule.CHARGED_CREEPERS).getSpawn()))
				((Creeper) e.getEntity()).setPowered(true);
	}
}
