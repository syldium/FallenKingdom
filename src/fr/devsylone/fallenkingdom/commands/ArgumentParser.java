package fr.devsylone.fallenkingdom.commands;

import fr.devsylone.fallenkingdom.exception.ArgumentParseException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.Version;
import fr.devsylone.fkpi.util.BlockDescription;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;

public class ArgumentParser {

    public static List<String> parsePlayers(CommandSender sender, String players) {
        if (Version.VersionType.V1_13.isHigherOrEqual() && players.startsWith("@")) {
            try {
                List<String> affected = getServer().selectEntities(sender, players).stream()
                        .filter(entity -> entity instanceof Player)
                        .map(Entity::getName)
                        .collect(Collectors.toList());
                if (affected.isEmpty()) {
                    TranslatableComponent notFound = new TranslatableComponent("argument.entity.notfound.player");
                    notFound.setColor(ChatColor.RED);
                    sender.spigot().sendMessage(notFound);
                }
                return affected;
            } catch (IllegalArgumentException e) {
                TranslatableComponent unknownOption = new TranslatableComponent("argument.entity.options.unknown");
                unknownOption.setColor(ChatColor.RED);
                unknownOption.addWith(players);
                sender.spigot().sendMessage(unknownOption);
                return Collections.emptyList();
            }

        }
        return Collections.singletonList(players);
    }

    public static boolean parseBoolean(String bool, Messages errorMessage) throws ArgumentParseException {
        if (!bool.equalsIgnoreCase("true") && !bool.equalsIgnoreCase("false")) {
            throw new ArgumentParseException(errorMessage.getMessage());
        }
        return Boolean.parseBoolean(bool);
    }

    public static BlockDescription parseBlock(String block) throws ArgumentParseException {
        Material m = Material.matchMaterial(block.substring(0, block.contains("[") ? block.indexOf("[") : block.length()));
        if (m == null || !m.isBlock()) {
            throw new ArgumentParseException(Messages.CMD_ERROR_UNKNOWN_BLOCK.getMessage().replace("%block%", block));
        }
        return new BlockDescription(m);
    }

    @SuppressWarnings("deprecation")
    public static BlockDescription parseBlock(int index, List<String> args, Player player, boolean denyAir) throws ArgumentParseException {
        if (index < args.size()) {
            return parseBlock(args.get(index));
        }
        Material m = player.getItemInHand().getType();
        if (!m.isBlock() || (denyAir && isAir(m))) {
            throw new ArgumentParseException(Messages.CMD_ERROR_UNKNOWN_BLOCK.getMessage().replace("%block%", m.name()));
        }
        return new BlockDescription(player.getItemInHand());
    }

    public static boolean isAir(Material material) {
        if (Version.VersionType.V1_13.isHigherOrEqual()) {
            return material.isAir();
        } else {
            return material.equals(Material.AIR);
        }
    }

    public static int parseInt(String nb, Messages errorMessage) throws ArgumentParseException {
        try {
            return Integer.parseInt(nb);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(errorMessage.getMessage());
        }
    }

    public static int parsePositiveInt(String nb, boolean includeOrigin, Messages errorMessage) throws ArgumentParseException {
        int integer = parseInt(nb, errorMessage);
        if (integer <= 0 && !includeOrigin || integer < 0) {
            throw new ArgumentParseException(errorMessage.getMessage());
        }
        return integer;
    }

    public static int parsePercentage(String percentage, Messages errorMessage) throws ArgumentParseException {
        int integer = parseInt(percentage, errorMessage);
        if (integer > 100 || integer < 0) {
            throw new ArgumentParseException(errorMessage.getMessage());
        }
        return integer;
    }

    public static int parseScoreboardLine(String line, Messages errorMessage) throws ArgumentParseException {
        int integer = parseInt(line, errorMessage);
        if (integer > 14 || integer < 0) {
            throw new ArgumentParseException(errorMessage.getMessage());
        }
        return integer;
    }

    public static int parseViewTime(String time, Messages errorMessage) throws ArgumentParseException {
        int integer = parseInt(time, errorMessage);
        if (integer > 30 || integer < 1) {
            throw new ArgumentParseException(errorMessage.getMessage());
        }
        return integer;
    }

    public static int parseOffset(String time, Messages errorMessage) throws IllegalArgumentException {
        int integer = parseInt(time, errorMessage);
        if (integer > 10 || integer < 1) {
            throw new IllegalArgumentException(errorMessage.getMessage());
        }
        return integer;
    }
}
