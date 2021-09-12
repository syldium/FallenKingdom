package fr.devsylone.fallenkingdom.players;

import fr.devsylone.fallenkingdom.display.GlobalDisplayService;
import fr.devsylone.fallenkingdom.scoreboard.FkScoreboard;
import org.jetbrains.annotations.NotNull;

import static org.mockito.Mockito.mock;

public class FkPlayerMock extends FkPlayer {

    private final FkScoreboard board = mock(FkScoreboard.class);

    public FkPlayerMock(@NotNull String name, @NotNull GlobalDisplayService displayService) {
        super(name, displayService);
    }

    @Override
    public void recreateScoreboard() {
        // do nothing
    }

    @Override
    public FkScoreboard getScoreboard() {
        return board;
    }
}
