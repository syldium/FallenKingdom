package fr.devsylone.fallenkingdom.display.sound;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

class LegacySoundPlayer implements SoundPlayer {

    protected static final String SOUND = "sound";
    protected static final String VOLUME = "volume";
    protected static final String PITCH = "pitch";

    protected final String sound;
    protected final float volume;
    protected final float pitch;

    LegacySoundPlayer(@NotNull String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    LegacySoundPlayer(@NotNull ConfigurationSection config) {
        this(config.getString(SOUND, ""), (float) config.getDouble(VOLUME, 0.8f), (float) config.getDouble(PITCH, 1f));
    }

    @Override
    public void play(@NotNull Player player) {
        player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
    }

    @Override
    @MustBeInvokedByOverriders
    public void save(@NotNull ConfigurationSection config) {
        config.set(SOUND, this.sound);
        config.set(VOLUME, this.volume);
        config.set(PITCH, this.pitch);
    }
}
