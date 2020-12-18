package fr.devsylone.fallenkingdom.commands;

import fr.devsylone.fallenkingdom.exception.ArgumentParseException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;

public class ArgumentParser {

    public static List<String> parsePlayers(CommandSender sender, String players) {
        if (!players.startsWith("@")) {
            return Collections.singletonList(players);
        }
        return parseEntities(sender, players).stream()
                .filter(entity -> entity instanceof Player)
                .map(Entity::getName)
                .collect(Collectors.toList());
    }

    public static List<Entity> parseEntities(CommandSender sender, String selector) {
        if (Version.VersionType.V1_13.isHigherOrEqual() && selector.startsWith("@")) {
            try {
                List<Entity> affected = getServer().selectEntities(sender, selector);
                if (affected.isEmpty()) {
                    TranslatableComponent notFound = new TranslatableComponent("argument.entity.notfound.entity");
                    notFound.setColor(ChatColor.RED);
                    sender.spigot().sendMessage(notFound);
                }
                return affected;
            } catch (IllegalArgumentException e) {
                TranslatableComponent unknownOption = new TranslatableComponent("argument.entity.options.unknown");
                unknownOption.setColor(ChatColor.RED);
                unknownOption.addWith(selector);
                sender.spigot().sendMessage(unknownOption);
                return Collections.emptyList();
            }

        }
        return sender instanceof Entity ? Collections.singletonList((Entity) sender) : Collections.emptyList();
    }

    public static boolean parseBoolean(String bool, Messages errorMessage) throws ArgumentParseException {
        if (!bool.equalsIgnoreCase("true") && !bool.equalsIgnoreCase("false")) {
            throw new ArgumentParseException(errorMessage.getMessage());
        }
        return Boolean.parseBoolean(bool);
    }

    public static MaterialWithData parseBlock(String block) throws ArgumentParseException {
        int sep = Version.VersionType.V1_13.isHigherOrEqual() ? -1 : block.indexOf(":");
        Material m = Material.matchMaterial(block.substring(0, sep < 0 ? block.length() : sep));
        if (m == null || !m.isBlock()) {
            throw new ArgumentParseException(Messages.CMD_ERROR_UNKNOWN_BLOCK.getMessage().replace("%block%", block));
        }
        byte data = -1;
        if (sep > 0) {
            data = (byte) parsePositiveInt(block.substring(sep + 1), true, Messages.CMD_ERROR_NAN);
        }
        return new MaterialWithData(m, data);
    }

    @SuppressWarnings("deprecation")
    public static MaterialWithData parseBlock(int index, List<String> args, Player player, boolean denyAir, boolean itemStackData) throws ArgumentParseException {
        if (index < args.size()) {
            return parseBlock(args.get(index));
        }
        ItemStack item = player.getItemInHand();
        Material m = item.getType();
        if (!m.isBlock() || (denyAir && isAir(m))) {
            throw new ArgumentParseException(Messages.CMD_ERROR_UNKNOWN_BLOCK.getMessage().replace("%block%", m.name()));
        }

        byte data = -1;
        if (itemStackData && !Version.VersionType.V1_13.isHigherOrEqual()) {
            data = item.getData().getData();
        }
        return new MaterialWithData(player.getItemInHand().getType(), data);
    }

    public static MaterialWithData parseBlock(int index, List<String> args, Player player, boolean denyAir) throws ArgumentParseException {
        return parseBlock(index, args, player, denyAir, false);
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

    public static int parseOffset(String time, Messages errorMessage) throws ArgumentParseException {
        int integer = parseInt(time, errorMessage);
        if (integer > 10 || integer < 1) {
            throw new ArgumentParseException(errorMessage.getMessage());
        }
        return integer;
    }

    public static LockedChest getLockedChest(String name) throws ArgumentParseException {
        for (LockedChest chest : FkPI.getInstance().getLockedChestsManager().getChestList()) {
            if (chest.getName().equalsIgnoreCase(name)) {
                return chest;
            }
        }
        throw new ArgumentParseException(Messages.CMD_ERROR_NOT_LOCKED_CHEST.getMessage());
    }

    @AllArgsConstructor @Getter
    public static class MaterialWithData {

        private final Material material;
        private final byte data;

        @Override
        public String toString() {
            if (data < 0) {
                return material.name();
            }
            return material.name() + ':' + data;
        }
    }
}
