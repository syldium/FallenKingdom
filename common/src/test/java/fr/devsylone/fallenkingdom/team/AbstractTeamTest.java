package fr.devsylone.fallenkingdom.team;

import fr.devsylone.fallenkingdom.util.StringBasedUUIDService;
import fr.devsylone.fkpi.team.FkTeam;
import org.jetbrains.annotations.NotNull;

abstract class AbstractTeamTest {

    protected final StringBasedUUIDService uuidService = new StringBasedUUIDService();

    protected @NotNull FkTeam.Builder builder() {
        return new TeamBuilderImpl(this.uuidService);
    }

    protected @NotNull FkTeam.Builder builder(@NotNull String name) {
        return this.builder().name(name);
    }
}
