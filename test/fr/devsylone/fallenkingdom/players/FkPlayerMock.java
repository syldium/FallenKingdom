package fr.devsylone.fallenkingdom.players;

import fr.devsylone.fallenkingdom.scoreboard.FkScoreboard;

import static org.mockito.Mockito.mock;

public class FkPlayerMock extends FkPlayer {

    private final FkScoreboard board = mock(FkScoreboard.class);

    public FkPlayerMock(String name) {
        super(name + "mock");
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
