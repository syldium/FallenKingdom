package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.version.title.TitleSender;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.ITeam;
import fr.devsylone.fkpi.teams.ChestsRoom;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ChestRoomRunnable extends BukkitRunnable {

    public static final int PERIOD_TICKS = 5;

    private final ChestsRoom chestsRoom;
    private final ITeam assailants;
    private final ITeam defenders;

    private int elapsedRuns;

    public ChestRoomRunnable(ChestsRoom chestsRoom, ITeam assailants, ITeam defenders) {
        this.chestsRoom = chestsRoom;
        this.assailants = assailants;
        this.defenders = defenders;
    }

    @Override
    public void run()
    {
        int captureTime = FkPI.getInstance().getChestsRoomsManager().getCaptureTime() * (Ticks.TICKS_PER_SECOND / PERIOD_TICKS);
        if (Fk.getInstance().getGame().isPaused()) {
            return;
        }
        if (++elapsedRuns == captureTime) {
            CaptureRunnable.run(defenders, assailants);
            chestsRoom.markAsCaptured();
            this.cancel();
        } else {
            List<Player> outsidePlayers = new LinkedList<>(); // Pour éviter une ConcurrentModificationException
            for (UUID uuid : chestsRoom.getEnemiesInside()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    continue;
                }
                if (!chestsRoom.contains(player.getLocation()) || player.isDead())
                    outsidePlayers.add(player);

                int progressionPercentage = elapsedRuns * 100 / captureTime;
                TitleSender.INSTANCE.sendTitle(player, "", "§b" + progressionPercentage + "%", 0, 20, 20);
            }
            for (Player player : outsidePlayers) {
                chestsRoom.removeEnemyInside(player);
            }
        }
    }
}
