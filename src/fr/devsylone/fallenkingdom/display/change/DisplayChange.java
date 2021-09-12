package fr.devsylone.fallenkingdom.display.change;

import fr.devsylone.fallenkingdom.display.DisplayService;
import fr.devsylone.fallenkingdom.display.DisplayType;
import org.jetbrains.annotations.NotNull;

public interface DisplayChange<S extends DisplayService> {

    @NotNull S apply(@NotNull S actual);

    @NotNull S revert(@NotNull S next);

    @NotNull DisplayType type();
}
