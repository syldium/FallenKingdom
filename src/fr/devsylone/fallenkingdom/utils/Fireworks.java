package fr.devsylone.fallenkingdom.utils;


import java.util.Random;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Fireworks {
  public static void random(Location loc) {
	  
    Firework firework1 = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
    FireworkMeta fireworkMeta = firework1.getFireworkMeta();
    Random random = new Random();
    FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(RandomColor.getColor(random.nextInt(17) + 1)).withFade(RandomColor.getColor(random.nextInt(17) + 1)).with(FireworkEffect.Type.BURST).trail(false).build();
    fireworkMeta.addEffect(effect);
    fireworkMeta.setPower(0);
    firework1.setFireworkMeta(fireworkMeta);
    firework1.detonate();

   
    }
 

}

