package fr.devsylone.fallenkingdom.display.sound;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static fr.devsylone.fallenkingdom.display.sound.LegacySoundPlayer.SOUND;
import static fr.devsylone.fallenkingdom.display.sound.LegacySoundPlayer.VOLUME;
import static fr.devsylone.fallenkingdom.version.Version.VersionType;
import static fr.devsylone.fallenkingdom.version.Version.classExists;

@FunctionalInterface
public interface SoundPlayer {

    SoundPlayer EMPTY = player -> {};
    boolean HAS_CATEGORY = classExists("org.bukkit.SoundCategory");

    void play(@NotNull Player player);

    default void save(@NotNull ConfigurationSection config) {

    }

    static @NotNull SoundPlayer fromConfig(@Nullable ConfigurationSection config, @NotNull String defaultSound) {
        if (config == null) {
            return create(defaultSound);
        } else if (config.getString(SOUND, "").isEmpty() || Double.compare(config.getDouble(VOLUME), 0) <= 0) {
            return EMPTY;
        }
        return create(config);
    }

    static @NotNull SoundPlayer create(@NotNull ConfigurationSection config) {
        return HAS_CATEGORY ? new ModernSoundPlayer(config) : new LegacySoundPlayer(config);
    }

    static @NotNull SoundPlayer create(@NotNull String sound) {
        return HAS_CATEGORY ? new ModernSoundPlayer(sound, 0.8f, 1f) : new LegacySoundPlayer(sound, 0.8f, 1f);
    }

    static @NotNull String deathSound() {
        if (VersionType.V1_9_V1_12.isHigherOrEqual()) {
            return "entity.wither.spawn";
        }
        return "mob.wither.spawn";
    }

    static @NotNull String gameStartSound() {
        if (VersionType.V1_9_V1_12.isHigherOrEqual()) {
            return "entity.generic.explode";
        }
        return "random.explode";
    }

    static @NotNull String eliminationSound() {
        if (VersionType.V1_13.isHigherOrEqual()) {
            return "entity.ender_dragon.death";
        } else if (VersionType.V1_9_V1_12.isHigherOrEqual()) {
            return "entity.enderdragon.death";
        }
        return "mob.enderdragon.end";
    }

    static @NotNull String eventSound() {
        if (VersionType.V1_13.isHigherOrEqual()) {
            return "entity.ender_dragon.growl";
        } else if (VersionType.V1_9_V1_12.isHigherOrEqual()) {
            return "entity.enderdragon.growl";
        }
        return "mob.enderdragon.growl";
    }
}
