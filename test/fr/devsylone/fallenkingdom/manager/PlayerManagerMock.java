package fr.devsylone.fallenkingdom.manager;

import fr.devsylone.fallenkingdom.display.GlobalDisplayService;
import fr.devsylone.fallenkingdom.players.FkPlayerMock;
import fr.devsylone.fallenkingdom.manager.saveable.PlayerManager;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PlayerManagerMock extends PlayerManager {

    public PlayerManagerMock(@NotNull GlobalDisplayService displayService) {
        super(displayService);
    }

    @Override
    public @NotNull List<@NotNull FkPlayer> getConnectedPlayers() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull List<@NotNull Player> getOnlinePlayers() {
        return Collections.emptyList();
    }

    @Override
    public FkPlayer getPlayer(String name) {
        FkPlayer fkPlayer = this.playersByString.get(name);
        if (fkPlayer != null) {
            return fkPlayer;
        }

        fkPlayer = new FkPlayerMock(name, this.displayService);
        playersByString.put(name, fkPlayer);
        return fkPlayer;
    }
}
