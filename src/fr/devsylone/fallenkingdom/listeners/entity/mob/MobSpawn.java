package fr.devsylone.fallenkingdom.listeners.entity.mob;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fkpi.rules.ChargedCreepers;

public class MobSpawn implements Listener {
    @EventHandler
    public void spawn(CreatureSpawnEvent e) {
        /*
         * PAUSE
         */
        e.setCancelled(Fk.getInstance().getGame().getState().equals(GameState.PAUSE));

        /*
         * CREEPER
         */

        if (e.getEntityType().equals(EntityType.CREEPER) && (new Random().nextInt(100) < (int)((ChargedCreepers) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("ChargedCreepers")).getSpawn()))
            ((Creeper) e.getEntity()).setPowered(true);


        /*
         * PHANTOMS
         */
        
        if (Bukkit.getServer().getClass().getPackage().getName().contains("1_13") || Bukkit.getServer().getClass().getPackage().getName().contains("1_14") || Bukkit.getServer().getClass().getPackage().getName().contains("1_15") || Bukkit.getServer().getClass().getPackage().getName().contains("1_16"))
            if (e.getEntity() instanceof org.bukkit.entity.Phantom && e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)
                    e.setCancelled(true);

    }
}