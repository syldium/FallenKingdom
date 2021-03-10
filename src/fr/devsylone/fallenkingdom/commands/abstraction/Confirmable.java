package fr.devsylone.fallenkingdom.commands.abstraction;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface Confirmable {

    int confirmationDelay = 10; // en secondes
    Map<UUID, Long> confirmed = new HashMap<>();
    // Sauvegarder l'instant où la commande a été exécutée permet d'éviter le cas où le BukkitRunnable soit annulé

    default boolean isConfirmed(CommandSender sender) {
        if (!(sender instanceof Player))
            return true;
        Long confirmation = confirmed.get(((Player) sender).getUniqueId());
        if (confirmation == null) {
            return false;
        }
        return confirmation > System.currentTimeMillis()-confirmationDelay*1000;
    }

    default void addConfirmed(CommandSender sender) {
        if (!(sender instanceof Player))
            return;
        UUID uuid = ((Player) sender).getUniqueId();
        confirmed.put(uuid, System.currentTimeMillis());
        Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> confirmed.remove(uuid),confirmationDelay * 20L);
    }

    default String createWarning(Messages warning, boolean format) {
        StringBuilder builder = new StringBuilder();
        if (format)
            builder.append("§c§m--------------§c ").append(Messages.WARNING.getMessage()).append(" §c§m--------------\n");
        builder.append(ChatColor.RESET).append(ChatColor.RED).append(warning.getMessage());
        if (format)
            builder.append("§c§m--------------------------------------");
        return builder.toString();
    }
}
