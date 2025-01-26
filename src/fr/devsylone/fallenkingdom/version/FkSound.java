package fr.devsylone.fallenkingdom.version;

import fr.devsylone.fallenkingdom.version.Version.VersionType;
import org.jetbrains.annotations.NotNull;

public enum FkSound {

    ENDERMAN_TELEPORT("mob.endermen.portal", "entity.endermen.teleport", "entity.enderman.teleport"),
    NOTE_BASS("note.bass", "block.note.bass", "block.note_block.bass"),
    NOTE_GUITAR("note.bassattack", "block.note.bass", "block.note_block.guitar"),
    NOTE_HARP("note.harp", "block.note.harp", "block.note_block.harp"),
    NOTE_PLING("note.pling", "block.note.xylophone", "block.note_block.pling");

    private final String key;

    FkSound(@NotNull String v1_8, @NotNull String v1_9, @NotNull String v1_13) {
        if (VersionType.V1_13.isHigherOrEqual()) {
            this.key = v1_13;
        } else if (VersionType.V1_9_V1_12.isHigherOrEqual()) {
            this.key = v1_9;
        } else {
            this.key = v1_8;
        }
    }

    public @NotNull String key() {
        return this.key;
    }
}
