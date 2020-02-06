package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import fr.devsylone.fallenkingdom.utils.Fireworks;

public class KillEvent implements Listener {
	    
    @EventHandler
    public void onEntityDeath(final EntityDeathEvent e) {
    	Entity DEADER = e.getEntity();
    	Entity KILLER = e.getEntity().getKiller();
    	
    	if(KILLER instanceof Player && DEADER instanceof Player) {
    		if(Bukkit.getServer().getClass().getPackage().getName().contains("1_8")) {
    			DEADER.getWorld().strikeLightningEffect(DEADER.getLocation());
    		}
    		
    		else {
    			for(int i =0; i<5;i++){
    				Fireworks.random(DEADER.getLocation());
	        	}
    			
        		for (Player player : Bukkit.getOnlinePlayers())
    				{	
    			
        			if (player.getName().equals(KILLER.getName())){
        				player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
        			}

    			}		
        	}
    	}
    }
}