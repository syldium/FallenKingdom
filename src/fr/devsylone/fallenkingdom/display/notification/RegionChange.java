package fr.devsylone.fallenkingdom.display.notification;

import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.Nexus;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Notifie un joueur lorsqu'il entre ou sort d'une base et/ou d'une salle des coffres.
 */
public class RegionChange implements GameNotification {

    private final Base base;
    private final Nexus nexus;
    private final MoveType moveType;
    private final long timestamp;

    public RegionChange(@NotNull Base base, @NotNull MoveType moveType) {
        this.base = base;
        this.nexus = null;
        this.moveType = moveType;
        this.timestamp = System.currentTimeMillis();
    }

    public RegionChange(@NotNull Nexus nexus, @NotNull MoveType moveType) {
        this.base = nexus.getBase();
        this.nexus = nexus;
        this.moveType = moveType;
        this.timestamp = System.currentTimeMillis();
    }

    public @NotNull Base getBase() {
        return this.base;
    }

    public @Nullable Nexus getNexus() {
        return this.nexus;
    }

    public @NotNull MoveType getMoveType() {
        return this.moveType;
    }

    @Override
    public long timestamp() {
        return this.timestamp;
    }

    @Override
    public @NotNull String message(@NotNull Player player) {
        Team pTeam = FkPI.getInstance().getTeamManager().getPlayerTeam(player);
        Team baseTeam = this.base.getTeam();
        Messages message;
        if (this.moveType == RegionChange.MoveType.ENTER) {
            if (this.nexus == null) {
                if (baseTeam.equals(pTeam)) {
                    message = Messages.PLAYER_SELF_BASE_ENTER;
                } else {
                    message = Messages.PLAYER_BASE_ENTER;
                }
            } else {
                if (baseTeam.equals(pTeam)) {
                    message = Messages.PLAYER_SELF_CHEST_ROOM_ENTER;
                } else {
                    message = Messages.PLAYER_CHEST_ROOM_ENTER;
                }
            }
        } else {
            if (this.nexus == null) {
                if (baseTeam.equals(pTeam)) {
                    message = Messages.PLAYER_SELF_BASE_EXIT;
                } else {
                    message = Messages.PLAYER_BASE_EXIT;
                }
            } else {
                if (baseTeam.equals(pTeam)) {
                    message = Messages.PLAYER_SELF_CHEST_ROOM_EXIT;
                } else {
                    message = Messages.PLAYER_CHEST_ROOM_EXIT;
                }
            }
        }
        return message.getMessage().replace("%team%", baseTeam.toString());
    }

    @Override
    public String toString() {
        return "RegionChange{base=" + this.base.getTeam() + ", nexus=" + (this.nexus != null) + ", moveType=" + this.moveType + ", timestamp=" + this.timestamp + "}";
    }

    public enum MoveType {
        ENTER,
        LEAVE
    }
}
