package fr.devsylone.fallenkingdom.commands.abstraction;

public enum CommandResult {
    SUCCESS(true),
    FAILURE(false),

    STATE_ERROR(false),
    INVALID_ARGS(false),
    NOT_VALID_EXECUTOR(false),
    NO_PERMISSION(false);

    private final boolean wasSuccess;

    CommandResult(boolean wasSuccess) {
        this.wasSuccess = wasSuccess;
    }

    public boolean wasFailure() {
        return !this.wasSuccess;
    }

}