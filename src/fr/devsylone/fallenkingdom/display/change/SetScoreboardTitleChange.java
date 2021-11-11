package fr.devsylone.fallenkingdom.display.change;

import fr.devsylone.fallenkingdom.display.DisplayType;
import fr.devsylone.fallenkingdom.display.ScoreboardDisplayService;
import org.jetbrains.annotations.NotNull;

public class SetScoreboardTitleChange implements DisplayChange<ScoreboardDisplayService> {

    private final String previous;
    private final String next;

    public SetScoreboardTitleChange(@NotNull ScoreboardDisplayService service, @NotNull String value) {
        this.previous = service.title();
        this.next = value;
    }

    @Override
    public @NotNull ScoreboardDisplayService apply(@NotNull ScoreboardDisplayService actual) {
        return actual.withTitle(this.next);
    }

    @Override
    public @NotNull ScoreboardDisplayService revert(@NotNull ScoreboardDisplayService next) {
        return next.withTitle(this.previous);
    }

    @Override
    public @NotNull DisplayType type() {
        return DisplayType.SCOREBOARD;
    }

    @Override
    public String toString() {
        return "SetScoreboardTitleChange{" +
                "previous='" + this.previous + '\'' +
                ", next='" + this.next + '\'' +
                '}';
    }
}
