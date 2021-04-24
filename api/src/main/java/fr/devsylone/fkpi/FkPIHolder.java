package fr.devsylone.fkpi;

import fr.devsylone.fkpi.team.TeamManager;
import org.jetbrains.annotations.NotNull;

final class FkPIHolder {

    final TeamManager teamManager;

    FkPIHolder(@NotNull TeamManager teamManager) {
        this.teamManager = teamManager;
    }
}
