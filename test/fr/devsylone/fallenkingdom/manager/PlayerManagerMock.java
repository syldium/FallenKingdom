package fr.devsylone.fallenkingdom.manager;

import fr.devsylone.fallenkingdom.players.FkPlayerMock;
import fr.devsylone.fallenkingdom.manager.saveable.PlayerManager;
import fr.devsylone.fallenkingdom.players.FkPlayer;

public class PlayerManagerMock extends PlayerManager {

    @Override
    public FkPlayer getPlayer(String name) {
        for(FkPlayer player : players)
            if(player.getName().equalsIgnoreCase(name))
                return player;

        return new FkPlayerMock(name);
    }
}
