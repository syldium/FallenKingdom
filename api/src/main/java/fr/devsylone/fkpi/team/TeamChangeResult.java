package fr.devsylone.fkpi.team;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class TeamChangeResult {

    private TeamChangeResult() {
    }

    public abstract boolean isSuccess();

    public abstract boolean isFailure();

    public abstract @NotNull Optional<@NotNull Throwable> failure();

    public abstract boolean isAlreadyIn();

    public abstract boolean isInTooManyTeams();

    private static final TeamChangeResult SUCCESS = new Success();
    public static @NotNull TeamChangeResult success() {
        return SUCCESS;
    }

    public static @NotNull TeamChangeResult failure(@NotNull Throwable failure) {
        return new Failure(failure);
    }

    private static final TeamChangeResult ALREADY_IN = new Failure(new AlreadyInTeamException());
    public static @NotNull TeamChangeResult alreadyIn() {
        return ALREADY_IN;
    }

    private static final TeamChangeResult IN_TOO_MANY_TEAMS = new Failure(new InTooManyTeamsException());
    public static @NotNull TeamChangeResult inTooManyTeams() {
        return IN_TOO_MANY_TEAMS;
    }

    private static final TeamChangeResult MISSING_PLAYER_NAME = new Failure(new PlayerProfileException("Unable to get the player's name."));
    public static @NotNull TeamChangeResult missingPlayerName() {
        return MISSING_PLAYER_NAME;
    }

    private static final TeamChangeResult MISSING_PLAYER_UUID = new Failure(new PlayerProfileException("Unable to get the player's unique identifier."));
    public static @NotNull TeamChangeResult missingPlayerId() {
        return MISSING_PLAYER_UUID;
    }

    private static final class Success extends TeamChangeResult {

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public @NotNull Optional<@NotNull Throwable> failure() {
            return Optional.empty();
        }

        @Override
        public boolean isAlreadyIn() {
            return false;
        }

        @Override
        public boolean isInTooManyTeams() {
            return false;
        }

        @Override
        public String toString() {
            return "TeamChangeResult{Success}";
        }
    }

    private static final class Failure extends TeamChangeResult {

        private final Throwable failure;

        private Failure(@NotNull Throwable failure) {
            this.failure = failure;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isAlreadyIn() {
            return this == ALREADY_IN;
        }

        @Override
        public boolean isInTooManyTeams() {
            return this == IN_TOO_MANY_TEAMS;
        }

        @Override
        public @NotNull Optional<@NotNull Throwable> failure() {
            return Optional.of(this.failure);
        }

        @Override
        public String toString() {
            return "TeamChangeResult{Failure=" + this.failure.getMessage() + '}';
        }
    }
}
