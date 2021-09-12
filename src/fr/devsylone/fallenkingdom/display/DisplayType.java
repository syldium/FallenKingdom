package fr.devsylone.fallenkingdom.display;

import java.util.Locale;

public enum DisplayType {
    ACTIONBAR,
    SCOREBOARD;

    public String asString() {
        return this.toString().toLowerCase(Locale.ROOT);
    }
}
