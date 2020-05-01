package fr.devsylone.fallenkingdom.commands.abstraction;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface Confirmable {
    List<UUID> confirmed = new ArrayList<>();

    default boolean isConfirmed(CommandSender sender) {
        if (!(sender instanceof Player))
            return true;
        return confirmed.contains(((Player) sender).getUniqueId());
    }

    default void addConfirmed(CommandSender sender) {
        if (!(sender instanceof Player))
            return;
        UUID uuid = ((Player) sender).getUniqueId();
        confirmed.add(uuid);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> confirmed.remove(uuid), 10 * 20);
    }

    default String createWarning(Messages warning, boolean format) {
        StringBuilder builder = new StringBuilder();
        if (format)
            builder.append("§c§m--------------§c ").append(Messages.WARNING.getMessage()).append("§c§m--------------\n");
        builder.append(ChatColor.RESET).append(ChatColor.RED).append(warning.getMessage());
        if (format)
            builder.append("§c§m--------------------------------------");
        return builder.toString();
    }
}
