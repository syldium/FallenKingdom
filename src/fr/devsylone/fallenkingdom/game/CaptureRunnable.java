package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.title.TitleSender;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.ITeam;
import fr.devsylone.fkpi.api.event.TeamCaptureEvent;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public final class CaptureRunnable {

    private CaptureRunnable() {}

    public static void run(@NotNull ITeam defenders, @NotNull ITeam assailants) {
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

        if (Fk.getInstance().getConfig().getBoolean("enable-mcfunction-support", false)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "function fallenkingdom:win");
        }

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
                    fw.setMetadata("nodamage", new FixedMetadataValue(Fk.getInstance(), true));
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(188, 166, 22), Color.GREEN).build());
                    fw.setFireworkMeta(meta);
                    fw.setVelocity(fw.getVelocity().multiply(0.2d));
                }
            }
        }.runTaskTimer(Fk.getInstance(), 20L, 20L);

        if (FkPI.getInstance().getRulesManager().getRule(Rule.AUTO_PAUSE).doAfterCapture()) {
            Fk.getInstance().getCommandManager().executeCommand(Fk.getInstance(), Bukkit.getConsoleSender(), "game pause");
        }
    }
}
