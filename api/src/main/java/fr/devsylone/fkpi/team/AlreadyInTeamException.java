package fr.devsylone.fkpi.team;

class AlreadyInTeamException extends IllegalStateException {

    private static final long serialVersionUID = -1879600089210669785L;

    AlreadyInTeamException() {
        super("The player is already in this team.");
    }
}
