package fr.devsylone.fallenkingdom.listeners.entity.player;

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

import fr.devsylone.fallenkingdom.Fk;


public class PotionListener implements Listener {

    //TODO: RELIER AVEC UNE COMMANDE 



    /*
     ***POTIONS DROPPER***
     */
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {

        Projectile projectile = e.getEntity();

        if (projectile instanceof ThrownPotion) {
                e.setCancelled(true);
            }
        }

    /*
     ***POTIONS CONSOMABLES 1.8***
     */
    
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();

        if (Bukkit.getServer().getClass().getPackage().getName().contains("1_8")) {

            if (e.getItem().getType() == Material.POTION) {
                e.setCancelled(true);
                player.sendMessage("§cLes potions sont désactivées durant cette partie.");
                player.setItemInHand(null);
            }
        }
    }


    /*
     ***POTIONS 1.9+
     */
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        Player player = (Player) event.getPlayer();

        if (Bukkit.getServer().getClass().getPackage().getName().contains("1_8")) {

        } else {

            if (event.getMaterial() == Material.POTION || event.getMaterial() == Material.SPLASH_POTION) {

                if (Fk.getInstance().getConfig().getBoolean("potions") == false) {

                    player.sendMessage("§cLes potions sont désactivées durant cette partie.");

                    event.setCancelled(true);
                    player.setItemInHand(null);
                }
            }
        }
    }
}