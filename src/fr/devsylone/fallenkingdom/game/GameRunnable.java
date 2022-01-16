package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.gamescommands.Pause;
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

import static fr.devsylone.fallenkingdom.display.tick.CycleTickFormatter.TICKS_PER_DAY_NIGHT_CYCLE;

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

            if(w.getEnvironment() == World.Environment.NORMAL && Math.abs(w.getFullTime() - worldTime) > 32 && game.time < game.timeFormat.dayDuration() && !(game.day == 0 && game.time < 20))
            {
                Bukkit.getLogger().info(Messages.CONSOLE_ADJUSTMENT_GAME_TIME.getMessage());
                game.time = game.timeFormat.timeFromWorld(w.getFullTime()) % TICKS_PER_DAY_NIGHT_CYCLE;
                game.day = game.timeFormat.dayFromWorld(w.getFullTime());
                worldTime = game.getExceptedWorldTime();
            }
            w.setFullTime(worldTime);
        }

        if(worldTime == 23000)
            Fk.broadcast(Messages.BROADCAST_SUN_WILL_RISE.getMessage());
    }

    protected void incrementDay()
    {
        game.day++;
        game.time = 0;
        DayEvent dayEvent = new DayEvent(DayEvent.Type.NEW_DAY, game.day, Messages.BROADCAST_DAY.getMessage().replace("%day%", String.valueOf(game.day))); //EVENT
        Bukkit.getPluginManager().callEvent(dayEvent);
        Fk.broadcast(dayEvent.getMessage());
        if (Fk.getInstance().getConfig().getBoolean("enable-mcfunction-support", false)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "function fallenkingdom:newday");
        }

        if (FkPI.getInstance().getRulesManager().getRule(Rule.DO_PAUSE_AFTER_DAY) && game.day > 1) {
            Fk.getInstance().getCommandManager().search(Pause.class).orElseThrow(RuntimeException::new).execute(Fk.getInstance(), Bukkit.getConsoleSender(), Collections.emptyList(), "fk");
        }

        DayEvent event = null;
        if (FkPI.getInstance().getRulesManager().getRule(Rule.PVP_CAP) == game.day) {
            game.pvpEnabled = true;
            event = new DayEvent(DayEvent.Type.PVP_ENABLED, game.day, Messages.BROADCAST_DAY_PVP.getMessage());
            Bukkit.getPluginManager().callEvent(event); //EVENT
        }

        if (FkPI.getInstance().getRulesManager().getRule(Rule.TNT_CAP) == game.day) {
            game.assaultsEnabled = true;
            event = new DayEvent(DayEvent.Type.TNT_ENABLED, game.day, Messages.BROADCAST_DAY_ASSAULT.getMessage());
            Bukkit.getPluginManager().callEvent(event); //EVENT
        }

        if (FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_CAP) == game.day) {
            game.netherEnabled = true;
            event = new DayEvent(DayEvent.Type.NETHER_ENABLED, game.day, Messages.BROADCAST_DAY_NETHER.getMessage());
            Bukkit.getPluginManager().callEvent(event); //EVENT
            Fk.getInstance().getPortalsManager().enablePortals();
        }

        if (FkPI.getInstance().getRulesManager().getRule(Rule.END_CAP) == game.day) {
            game.endEnabled = true;
            event = new DayEvent(DayEvent.Type.END_ENABLED, game.day, Messages.BROADCAST_DAY_END.getMessage());
            Bukkit.getPluginManager().callEvent(event); //EVENT
        }

        if (event != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Fk.getInstance().getWorldManager().isAffected(player.getWorld())) {
                    Fk.getInstance().getDisplayService().playEventSound(player);
                }
            }
        }

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
        Fk.getInstance().getDisplayService().updateAll();
    }
}
