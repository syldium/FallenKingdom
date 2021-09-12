package fr.devsylone.fallenkingdom.display.change;

import fr.devsylone.fallenkingdom.display.DisplayType;
import fr.devsylone.fallenkingdom.display.ScoreboardDisplayService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetScoreboardLineChange implements DisplayChange<ScoreboardDisplayService> {

    private final int line;
    private final String previous;
    private final String next;

    public SetScoreboardLineChange(@NotNull ScoreboardDisplayService scoreboard, int line, @Nullable String next) {
        this.line = line;
        this.previous = line < scoreboard.size() ? scoreboard.line(line) : null;
        this.next = next;
    }

    @Override
    public @NotNull ScoreboardDisplayService apply(@NotNull ScoreboardDisplayService actual) {
        return actual.withLine(this.line, this.next);
    }

    @Override
    public @NotNull ScoreboardDisplayService revert(@NotNull ScoreboardDisplayService next) {
        return next.withLine(this.line, this.previous);
    }

    @Override
    public @NotNull DisplayType type() {
        return DisplayType.SCOREBOARD;
    }

    @Override
    public String toString() {
        return "SetScoreboardLineChange{" +
                "line=" + this.line +
                ", previous='" + this.previous + '\'' +
                ", next='" + this.next + '\'' +
                '}';
    }
}
