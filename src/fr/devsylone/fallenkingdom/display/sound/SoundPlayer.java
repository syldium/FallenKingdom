package fr.devsylone.fallenkingdom.display.sound;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.devsylone.fallenkingdom.version.Version.VersionType;
import static fr.devsylone.fallenkingdom.version.Version.classExists;

@FunctionalInterface
public interface SoundPlayer {

    SoundPlayer EMPTY = player -> {};
    boolean HAS_CATEGORY = classExists("org.bukkit.SoundCategory");

    void play(@NotNull Player player);

    default void save(@NotNull ConfigurationSection config) {

    }

    static @NotNull SoundPlayer create(@NotNull ConfigurationSection config) {
        return HAS_CATEGORY ? new ModernSoundPlayer(config) : new LegacySoundPlayer(config);
    }

    static @NotNull SoundPlayer create(@NotNull String sound) {
        return HAS_CATEGORY ? new ModernSoundPlayer(sound, 0.8f, 1f) : new LegacySoundPlayer(sound, 0.8f, 1f);
    }

    static @NotNull SoundPlayer deathSound() {
        if (VersionType.V1_9_V1_12.isHigherOrEqual()) {
            return create("entity.wither.spawn");
        }
        return create("mob.wither.spawn");
    }

    static @NotNull SoundPlayer eliminationSound() {
        if (VersionType.V1_13.isHigherOrEqual()) {
            return create("entity.ender_dragon.death");
        } else if (VersionType.V1_9_V1_12.isHigherOrEqual()) {
            return create("entity.enderdragon.death");
        }
        return create("mob.enderdragon.end");
    }
}
