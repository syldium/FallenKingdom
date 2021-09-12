package fr.devsylone.fallenkingdom.display.change;

import fr.devsylone.fallenkingdom.display.DisplayType;
import fr.devsylone.fallenkingdom.display.SimpleDisplayService;
import org.jetbrains.annotations.NotNull;

public class SetTextChange implements DisplayChange<SimpleDisplayService> {

    private final DisplayType displayType;
    private final String previous;
    private final String next;

    public SetTextChange(@NotNull SimpleDisplayService service, @NotNull String value) {
        this.displayType = service.type();
        this.previous = service.value();
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
