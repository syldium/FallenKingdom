package fr.devsylone.fallenkingdom.display;

import java.util.Locale;

public enum DisplayType {
    ACTIONBAR,
    BOSSBAR,
    SCOREBOARD;

    public String asString() {
        return this.toString().toLowerCase(Locale.ROOT);
    }
}
