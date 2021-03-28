package fr.devsylone.fallenkingdom.listeners.entity.player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {

    @EventHandler
    public void onSuccess(PlayerAdvancementDoneEvent event) {
        for (LockedChest chest : FkPI.getInstance().getLockedChestsManager().getChests()) {
            if (chest.getRequiredAdvancement() == null || chest.getUnlockDay() > Fk.getInstance().getGame().getDay()) {
                continue;
            }
            if (event.getAdvancement().getKey().equals(chest.getRequiredAdvancement().getKey())) {
                Bukkit.getScheduler().runTaskLater(Fk.getInstance(), () -> {
                    ChatUtils.sendMessage(event.getPlayer(), ChatColor.YELLOW + "==========" + ChatColor.MAGIC + "==========" + ChatColor.RESET + "" + ChatColor.YELLOW + "============");
                    ChatUtils.sendMessage(event.getPlayer(), Messages.PLAYER_LOCKED_CHEST_ACCESS_UNLOCKED.getMessage()
                            .replace("%x%", String.valueOf(chest.getLocation().getBlockX()))
                            .replace("%y%", String.valueOf(chest.getLocation().getBlockY()))
                            .replace("%z%", String.valueOf(chest.getLocation().getBlockZ())));
                    ChatUtils.sendMessage(event.getPlayer(), ChatColor.YELLOW + "==========" + ChatColor.MAGIC + "==========" + ChatColor.RESET + "" + ChatColor.YELLOW + "============");
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1L, 1L);
                }, 1L);
            }
        }
    }
}
