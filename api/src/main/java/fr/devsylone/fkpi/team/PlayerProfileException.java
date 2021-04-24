package fr.devsylone.fkpi.team;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

class PlayerProfileException extends IllegalArgumentException implements Serializable {

    private static final long serialVersionUID = -2813623510226887333L;

    PlayerProfileException(@NotNull String message) {
        super(message);
    }
}
