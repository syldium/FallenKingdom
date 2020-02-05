package fr.devsylone.fallenkingdom.utils;


import java.util.Random;

import org.bukkit.Color;
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
    FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(getColor(random.nextInt(17) + 1)).withFade(getColor(random.nextInt(17) + 1)).with(FireworkEffect.Type.BURST).trail(false).build();
    fireworkMeta.addEffect(effect);
    fireworkMeta.setPower(0);
    firework1.setFireworkMeta(fireworkMeta);
    firework1.detonate();

   
    }
  
  private static Color getColor(int i) {
    switch (i) {
      case 1:
        return Color.AQUA;
      case 2:
        return Color.BLACK;
      case 3:
        return Color.BLUE;
      case 4:
        return Color.FUCHSIA;
      case 5:
        return Color.GRAY;
      case 6:
        return Color.GREEN;
      case 7:
        return Color.LIME;
      case 8:
        return Color.MAROON;
      case 9:
        return Color.NAVY;
      case 10:
        return Color.OLIVE;
      case 11:
        return Color.ORANGE;
      case 12:
        return Color.PURPLE;
      case 13:
        return Color.RED;
      case 14:
        return Color.SILVER;
      case 15:
        return Color.TEAL;
      case 16:
        return Color.WHITE;
      case 17:
        return Color.YELLOW;
    } 
    return null;
  }

}

