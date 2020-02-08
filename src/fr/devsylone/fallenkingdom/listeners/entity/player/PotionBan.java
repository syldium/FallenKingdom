package fr.devsylone.fallenkingdom.listeners.entity.player;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;

import fr.devsylone.fallenkingdom.Fk;


public class PotionBan implements Listener {
	
	//TODO: RELIER AVEC UNE COMMANDE 
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) { //POTION LANCÉE 1.8

		Projectile projectile = e.getEntity();

	    if (projectile instanceof ThrownPotion) {
	       ThrownPotion pot = (ThrownPotion) projectile;
	       Collection<PotionEffect> effects = pot.getEffects();
	       for (@SuppressWarnings("unused") PotionEffect p : effects) {
	               e.setCancelled(true);
	               break;
	       }
	 }	
}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onItemConsume (PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		
		if(Bukkit.getServer().getClass().getPackage().getName().contains("1_8")) {
		    
	     if (e.getItem().getType() == Material.POTION) {
	    	 e.setCancelled(true);
	    	 player.sendMessage("§cLes potions sont désactivées durant cette partie.");
	    	 player.setItemInHand(null);
	     	}
		}
	}
	

	@SuppressWarnings("deprecation")
	@EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
		Player player = (Player) event.getPlayer();
		
		if(Bukkit.getServer().getClass().getPackage().getName().contains("1_8")) {
			
		}
		
		else { 
			
			if(event.getMaterial() ==  Material.POTION || event.getMaterial() ==  Material.SPLASH_POTION) {
      
    		if(Fk.getInstance().getConfig().getBoolean("potions") == false) {
	            
    			player.sendMessage("§cLes potions sont désactivées durant cette partie.");
	          
	            event.setCancelled(true);
	            player.setItemInHand(null);        
        		}
			}
		}
	}
}
	
