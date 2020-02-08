package fr.devsylone.fallenkingdom.utils;


import java.util.Random;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworksUtils {
  public static void random(Location loc) {
	  
    Firework firework = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
    FireworkMeta fireworkMeta = firework.getFireworkMeta();
    Random random = new Random();
    FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(ColorsUtils.getColors(random.nextInt(17) + 1)).withFade(ColorsUtils.getColors(random.nextInt(17) + 1)).with(FireworkEffect.Type.BURST).trail(false).build();
    fireworkMeta.addEffect(effect);
    fireworkMeta.setPower(0);
    firework.setFireworkMeta(fireworkMeta);
    firework.detonate();
    }
}

