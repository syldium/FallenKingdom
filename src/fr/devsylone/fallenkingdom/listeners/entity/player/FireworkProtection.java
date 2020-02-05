package fr.devsylone.fallenkingdom.listeners.entity.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FireworkProtection implements Listener {
  
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof org.bukkit.entity.Firework)
      event.setCancelled(true); 
  }
}
