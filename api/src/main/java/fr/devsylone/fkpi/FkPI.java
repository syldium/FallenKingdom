package fr.devsylone.fkpi;

import fr.devsylone.fkpi.team.TeamManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FkPI {

    static FkPIHolder HOLDER;

    private FkPI() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static @NotNull TeamManager teams() {
        return assertInitialized(HOLDER).teamManager;
    }

    private static <T> @NotNull T assertInitialized(@Nullable T instance) {
        if (instance == null) {
            throw new IllegalStateException("The plugin has not been initialized!");
        }
        return instance;
    }
}
