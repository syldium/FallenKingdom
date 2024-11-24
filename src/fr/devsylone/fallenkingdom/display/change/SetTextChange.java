package fr.devsylone.fallenkingdom.display.change;

import fr.devsylone.fallenkingdom.display.DisplayType;
import fr.devsylone.fallenkingdom.display.SimpleDisplayService;
import fr.devsylone.fallenkingdom.display.content.Content;
import org.jetbrains.annotations.NotNull;

public class SetTextChange implements DisplayChange<SimpleDisplayService> {

    private final DisplayType displayType;
    private final Content previous;
    private final Content next;

    public SetTextChange(@NotNull SimpleDisplayService service, @NotNull Content value) {
        this.displayType = service.type();
        this.previous = service.content();
        this.next = value;
    }

    @Override
    public @NotNull SimpleDisplayService apply(@NotNull SimpleDisplayService actual) {
        return actual.withValue(this.next);
    }

    @Override
    public @NotNull SimpleDisplayService revert(@NotNull SimpleDisplayService next) {
        return next.withValue(this.previous);
    }

    @Override
    public @NotNull DisplayType type() {
        return this.displayType;
    }

    @Override
    public String toString() {
        return "SetTextChange{" +
                "displayType=" + this.displayType +
                ", previous='" + this.previous + '\'' +
                ", next='" + this.next + '\'' +
                '}';
    }
}
