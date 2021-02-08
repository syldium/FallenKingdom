package fr.devsylone.fallenkingdom.utils;

import fr.devsylone.fallenkingdom.pause.PausedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public final class ConfigHelper {

    private final static Pattern SEPARATOR = Pattern.compile(":");

    private ConfigHelper() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static void loadSectionsWithIndex(
            @Nullable ConfigurationSection section,
            @NotNull BiConsumer<@NotNull Integer, @NotNull ConfigurationSection> consumer
    ) {
        if (section == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            try {
                int index = Integer.parseInt(entry.getKey());
                for (Object object : ((ConfigurationSection) entry.getValue()).getValues(false).values()) {
                    if (object instanceof ConfigurationSection) {
                        consumer.accept(index, (ConfigurationSection) object);
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Contract("_, _, !null -> !null")
    public static <E extends Enum<E>> @Nullable E enumValueOf(@NotNull Class<E> enumClass, @Nullable String value, @Nullable E def) {
        if (value == null) {
            return def;
        }

        try {
            return Enum.valueOf(enumClass, value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return def;
        }
    }

    public static @Nullable World getWorld(@Nullable String string) {
        if (string == null) {
            return null;
        }

        try {
            return Bukkit.getWorld(UUID.fromString(string));
        } catch (IllegalArgumentException e) {
            return Bukkit.getWorld(string);
        }
    }

    public static @Nullable Location getLocation(@Nullable ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        String worldString = section.getString("world");
        if (worldString == null) {
            return null;
        }

        World world = getWorld(worldString);
        if (world == null) {
            return null;
        }

        return new Location(
                world,
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch")
        );
    }

    public static @Nullable Location getLocation(@Nullable String string) {
        if (string == null) {
            return null;
        }

        String[] split = SEPARATOR.split(string);
        if (split.length < 4) {
            return null;
        }

        World world = getWorld(split[0]);
        if (world == null) {
            return null;
        }

        try {
            return new Location(world, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Contract("!null -> !null")
    public static @Nullable String serializeBlockPos(@Nullable Location location) {
        if (location == null) {
            return null;
        }

        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    public static void setLocation(@NotNull ConfigurationSection section, @Nullable Location location) {
        if (location != null) {
            section.set("world", location.getWorld() == null ? null : location.getWorld().getUID().toString());
            section.set("x", location.getX());
            section.set("y", location.getY());
            section.set("z", location.getZ());
            section.set("yaw", location.getYaw());
            section.set("pitch", location.getPitch());
        }
    }
}
