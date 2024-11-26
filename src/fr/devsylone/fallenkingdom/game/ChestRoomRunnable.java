package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.title.TitleSender;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.ITeam;
import fr.devsylone.fkpi.api.event.TeamCaptureEvent;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.ChestsRoom;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
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
            Fk.broadcast("");
            Fk.broadcast(
                    Messages.BROADCAST_CHEST_ROOM_CAPTURED.getMessage()
                            .replace("%assailants%", assailants.toString())
                            .replace("%defenders%", defenders.toString())
            );
            Fk.broadcast("");

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!Fk.getInstance().getWorldManager().isAffected(player.getWorld())) {
                    continue;
                }

                TitleSender.INSTANCE.sendTitle(
                        player,
                        Messages.BROADCAST_CHEST_ROOM_TITLE.getMessage().replace("%defenders%", defenders.toString()).replace("%assailants%", assailants.toString()),
                        Messages.BROADCAST_CHEST_ROOM_SUBTITLE.getMessage(),
                        10, 60, 10
                );
            }

            Bukkit.getServer().getPluginManager().callEvent(new TeamCaptureEvent(assailants, defenders, true)); // EVENT

            if (FkPI.getInstance().getRulesManager().getRule(Rule.AUTO_PAUSE).doAfterCapture()) {
                Fk.getInstance().getCommandManager().executeCommand(Fk.getInstance(), Bukkit.getConsoleSender(), "game pause");
                this.cancel();
                return;
            }

            if (Fk.getInstance().getConfig().getBoolean("enable-mcfunction-support", false))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "function fallenkingdom:win");

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!Fk.getInstance().getWorldManager().isAffected(p.getWorld())) {
                    continue;
                }

                TitleSender.INSTANCE.sendTitle(p, Messages.BROADCAST_VICTORY_TITLE.getMessage(), Messages.BROADCAST_VICTORY_SUBTITLE.getMessage().replace("%assailants%", assailants.toString()), 10, 10 * 20, 10);
            }

            new BukkitRunnable()
            {
                private int i;

                @Override
                public void run()
                {
                    if (i++ >= 20) {
                        this.cancel();
                    }
                    for (String playerName : assailants.getPlayers())
                    {
                        Player player = Bukkit.getPlayer(playerName);
                        if (player == null) {
                            continue;
                        }
                        Firework fw = player.getWorld().spawn(player.getLocation(), Firework.class);
                        FireworkMeta meta = fw.getFireworkMeta();
                        meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(188, 166, 22), Color.GREEN).build());
                        fw.setFireworkMeta(meta);
                        fw.setVelocity(fw.getVelocity().multiply(0.2d));
                    }
                }
            }.runTaskTimer(Fk.getInstance(), 20L, 20L);

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
