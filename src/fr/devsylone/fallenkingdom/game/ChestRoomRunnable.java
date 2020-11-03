package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.ITeam;
import fr.devsylone.fkpi.api.event.TeamCaptureEvent;
import fr.devsylone.fkpi.teams.ChestsRoom;
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

    private final ChestsRoom chestsRoom;
    private final ITeam assailants;
    private final ITeam defenders;

    private final long startCaptureTimestamp = System.currentTimeMillis();
    private final long captureTime = FkPI.getInstance().getChestsRoomsManager().getCaptureTime() * 1000;

    public ChestRoomRunnable(ChestsRoom chestsRoom, ITeam assailants, ITeam defenders) {
        this.chestsRoom = chestsRoom;
        this.assailants = assailants;
        this.defenders = defenders;
    }

    @Override
    public void run()
    {
        if (System.currentTimeMillis() >= startCaptureTimestamp + captureTime) {
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

                Fk.getInstance().getPacketManager().sendTitle(
                        player,
                        Messages.BROADCAST_CHEST_ROOM_TITLE.getMessage().replace("%defenders%", defenders.toString()).replace("%assailants%", assailants.toString()),
                        Messages.BROADCAST_CHEST_ROOM_SUBTITLE.getMessage(),
                        10, 60, 10
                );
            }

            Bukkit.getServer().getPluginManager().callEvent(new TeamCaptureEvent(assailants, defenders, true)); // EVENT

            if (FkPI.getInstance().getTeamManager().getTeams().size() > 2) {
                Fk.getInstance().getCommandManager().executeCommand(Fk.getInstance(), Bukkit.getConsoleSender(), "game pause");
                return;
            }

            if (Fk.getInstance().getConfig().getBoolean("enable-mcfunction-support", false))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "function fallenkingdom:win");

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!Fk.getInstance().getWorldManager().isAffected(p.getWorld())) {
                    continue;
                }

                Fk.getInstance().getPacketManager().sendTitle(p, Messages.BROADCAST_VICTORY_TITLE.getMessage(), Messages.BROADCAST_VICTORY_SUBTITLE.getMessage().replace("%assailants%", assailants.toString()), 10, 10 * 20, 10);
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

                Fk.getInstance().getPacketManager().sendTitle(player, "", "§b" + (int) ((System.currentTimeMillis() - startCaptureTimestamp) / 1000.0d / (double) FkPI.getInstance().getChestsRoomsManager().getCaptureTime() * 100) + "%", 0, 20, 20);
            }
            for (Player player : outsidePlayers) {
                chestsRoom.removeEnemyInside(player);
            }
        }
    }
}
