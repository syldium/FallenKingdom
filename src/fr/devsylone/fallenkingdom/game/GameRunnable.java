package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.gamescommands.Pause;
import fr.devsylone.fallenkingdom.display.tick.CycleTickFormatter;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.DayEvent;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

class GameRunnable extends BukkitRunnable
{
    protected final Game game;
    private int lastMinutes;
    
    GameRunnable(Game game)
    {
        this.game = game;
        game.updateDayDuration();
        for (World world : Bukkit.getWorlds()) {
            if (Fk.getInstance().getWorldManager().isAffected(world)) {
                world.setFullTime(game.getExceptedWorldTime());
            }
        }
    }

    @Override
    public void run()
    {
        if(game.state != Game.GameState.STARTED)
        {
            Fk.getInstance().getLogger().warning("Game is not running. Cancelling game task.");
            game.task = null;
            this.cancel();
            return;
        }

        game.time++;
        updateWorldTime();

        if(game.time >= game.timeFormat.dayDuration())
            incrementDay();

        int minutes = game.timeFormat.extractMinutes(game.time);
        if(minutes != lastMinutes)
        {
            Fk.getInstance().getDisplayService().updateAll(PlaceHolder.DAY, PlaceHolder.HOUR, PlaceHolder.MINUTE);
            lastMinutes = minutes;
        }
    }

    protected void updateWorldTime()
    {
        long worldTime = game.getExceptedWorldTime();
        for(World w : Bukkit.getWorlds())
        {
            if(!Fk.getInstance().getWorldManager().isAffected(w))
                continue;

            if(w.getEnvironment() == World.Environment.NORMAL && Math.abs(w.getFullTime() - worldTime) > 32 && game.day != 0)
            {
                Bukkit.getLogger().info(Messages.CONSOLE_ADJUSTMENT_GAME_TIME.getMessage());
                final long fullTime = w.getFullTime();
                final long day = game.timeFormat.dayFromWorld(fullTime);
                game.time = game.timeFormat.timeFromWorld(fullTime) % game.timeFormat.dayDuration();
                if (day > 0) {
                    game.day = game.timeFormat.dayFromWorld(fullTime);
                }
                worldTime = game.getExceptedWorldTime();
            }
            w.setFullTime(worldTime);
        }

        if(game.time == game.timeFormat.dayDuration() - game.timeFormat.dayDuration() / CycleTickFormatter.HOURS_PER_DAY)
            Fk.broadcast(Messages.BROADCAST_SUN_WILL_RISE.getMessage());
    }

    protected void incrementDay() {
        game.day++;
        game.time = 0;
        DayEvent dayEvent = new DayEvent(DayEvent.Type.NEW_DAY, game.day, Messages.BROADCAST_DAY.getMessage().replace("%day%", String.valueOf(game.day))); //EVENT
        Bukkit.getPluginManager().callEvent(dayEvent);
        Fk.broadcast(dayEvent.getMessage());
        if (Fk.getInstance().getConfig().getBoolean("enable-mcfunction-support", false)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "function fallenkingdom:newday");
        }

        if (FkPI.getInstance().getRulesManager().getRule(Rule.AUTO_PAUSE).doAfterDay() && game.day > 1) {
            Fk.getInstance().getCommandManager().search(Pause.class).orElseThrow(RuntimeException::new).execute(Fk.getInstance(), Bukkit.getConsoleSender(), Collections.emptyList(), "fk");
        }

        if (game.updateCaps()) {
            Fk.getInstance().getDisplayService().updateAll();
        }
        updateLockedChests();
    }

    private void updateLockedChests() {
        for (LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChests()) {
            if (chest.getUnlockDay() != game.day) {
                continue;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!chest.hasAccess(player)) {
                    continue;
                }
                ChatUtils.sendMessage(player, Messages.BROADCAST_DAY_CHEST.getMessage()
                        .replace("%name%", chest.getName())
                        .replace("%x%", String.valueOf(chest.getLocation().getBlockX()))
                        .replace("%y%", String.valueOf(chest.getLocation().getBlockY()))
                        .replace("%z%", String.valueOf(chest.getLocation().getBlockZ()))
                );
                player.playSound(player.getLocation(), FkSound.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
            }
        }
    }
}
