package fr.devsylone.fkpi.api.event;

import fr.devsylone.fkpi.api.ITeam;

public class TeamCaptureEvent extends FkEvent
{
    private final ITeam assailants;
    private final ITeam defenders;
    private final boolean isSuccess;

    public TeamCaptureEvent(ITeam assailants, ITeam defenders, boolean isSuccess)
    {
        this.assailants = assailants;
        this.defenders = defenders;
        this.isSuccess = isSuccess;
    }

    public ITeam getAssailantsTeam()
    {
        return assailants;
    }

    public ITeam getDefendersTeam()
    {
        return defenders;
    }

    public boolean isSuccess()
    {
        return isSuccess;
    }
}
