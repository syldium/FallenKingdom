package fr.devsylone.fkpi.team;

public class InTooManyTeamsException extends IllegalStateException {

    private static final long serialVersionUID = 3097876543457478007L;

    public InTooManyTeamsException() {
        super("This player is already in too many teams!");
    }
}
