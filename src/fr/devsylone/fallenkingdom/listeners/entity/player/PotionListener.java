package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fkpi.rules.DenyPotions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Arrays;

public class PotionListener implements Listener
{
    @EventHandler
    public void onPotionBrew(final BrewEvent event)
    {
        // En 1.8, getContents() donne les références des items, donc on perd l'aspect save des potions. Du coup, on fait une deep copy.
        ItemStack[] brewerInventory = Arrays.stream(event.getContents().getContents()).map(item -> item == null ? null : item.clone()).toArray(ItemStack[]::new);
        ItemStack brewed = event.getContents().getIngredient().clone();

        DenyPotions rule = (DenyPotions) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DenyPotions");
        Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
            for (ItemStack item : event.getContents().getContents())
            {
                if (item != null && item.getItemMeta() instanceof PotionMeta)
                {
                    if (rule.isProhibited(item))
                    {
                        event.getContents().setContents(brewerInventory);
                        event.getContents().setIngredient(null);
                        event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation(), brewed);

                        String message = ChatUtils.PREFIX + ChatColor.RED + (((boolean) rule.getValue()) ? "L" : "C") + "es potions sont désactivées durant cette partie.";
                        event.getContents().getViewers().forEach(viewer -> viewer.sendMessage(message));
                        break;
                    }
                }
            }
        }, 1);
    }
}