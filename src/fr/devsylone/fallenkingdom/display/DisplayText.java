package fr.devsylone.fallenkingdom.display;

import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class DisplayText implements Saveable {

    private static final String[] DEF_BOOLEANS = new String[]{"§2✔", "§4✘"};

    private String stringTrue = DEF_BOOLEANS[0],
            stringFalse = DEF_BOOLEANS[1],
            noInfo = "§4?",
            arrows = this.defaultArrows();

    public @NotNull String format(boolean value) {
        return value ? stringTrue : stringFalse;
    }

    public @NotNull String noInfo() {
        return this.noInfo;
    }

    public @NotNull String arrows() {
        return this.arrows;
    }

    public char arrowAt(@Range(from = 0, to = 360) int angle) {
        return this.arrows.charAt(angle / 45);
    }

    @Override
    public void load(ConfigurationSection config) {
        String[] booleans = config.getString("Boolean", "§2✔:§4✘").split(":");
        if (booleans.length == 1) {
            booleans = DEF_BOOLEANS;
        }
        this.stringTrue = booleans[0];
        this.stringFalse = booleans[1];

        this.noInfo = config.getString("NoInfo", "§4?");

        final String defaultArrows = this.defaultArrows();
        String arrows = config.getString("Arrows", defaultArrows);
        if (arrows.length() < defaultArrows.length()) {
            arrows = defaultArrows;
        }
        this.arrows = arrows;
    }

    @Override
    public void save(ConfigurationSection config) {
        config.set("Boolean", this.stringTrue + ':' + this.stringFalse);
        config.set("NoInfo", this.noInfo);
        config.set("Arrows", this.arrows);
    }

    private @NotNull String defaultArrows() {
        if (Version.VersionType.V1_16.isHigherOrEqual()) {
            return "⇑⇗⇛⇙⇓⇘⇐⇖"; // Workaround https://bugs.mojang.com/browse/MC-179867
        } else if (Version.VersionType.V1_13.isHigherOrEqual()) {
            return "⇑⇗⇒⇘⇓⇙⇐⇖";
        }
        return "↑↗→↘↓↙←↖";
    }
}
