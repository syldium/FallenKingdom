package fr.devsylone.fallenkingdom.display.sound;

import fr.devsylone.fallenkingdom.utils.ConfigHelper;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class ModernSoundPlayer extends LegacySoundPlayer {

    protected static final String CATEGORY = "category";

    private final SoundCategory category;

    ModernSoundPlayer(@NotNull String sound, float volume, float pitch) {
        this(sound, SoundCategory.PLAYERS, volume, pitch);
    }

    ModernSoundPlayer(@NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
        this(sound.getKey().getKey(), category, volume, pitch);
    }

    ModernSoundPlayer(@NotNull String sound, @NotNull SoundCategory category, float volume, float pitch) {
        super(sound, volume, pitch);
        this.category = category;
    }

    ModernSoundPlayer(@NotNull ConfigurationSection config) {
        super(config);
        this.category = ConfigHelper.enumValueOf(SoundCategory.class, config.getString(CATEGORY), SoundCategory.MASTER);
    }

    @Override
    public void play(@NotNull Player player) {
        player.playSound(player.getLocation(), this.sound, this.category, this.volume, this.pitch);
    }

    @Override
    public void save(@NotNull ConfigurationSection section) {
        super.save(section);
        section.set(CATEGORY, this.category.name());
    }
}
