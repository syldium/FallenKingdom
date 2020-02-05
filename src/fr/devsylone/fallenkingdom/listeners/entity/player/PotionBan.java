package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;


public class PotionBan implements Listener {

	boolean MODULE_STATE = true;
	
	@SuppressWarnings("deprecation")
	@EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
		if(MODULE_STATE == true) {
        if (event.getMaterial() ==  Material.POTION || event.getMaterial() ==  Material.SPLASH_POTION || event.getMaterial() ==  Material.LEGACY_SPLASH_POTION) {
            Player player = (Player) event.getPlayer();
            
            player.sendMessage("§cLes potions sont désactivées sur cette partie.");
          
            event.setCancelled(true);
            
            player.setItemInHand(null);        
          
        	}
        
		}
	}
	
}
