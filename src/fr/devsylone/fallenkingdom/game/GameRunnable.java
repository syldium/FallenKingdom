package fr.devsylone.fallenkingdom.game;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.game.gamescommands.Pause;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.DayEvent;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.Saveable;
import lombok.Getter;

@Getter
public class GameRunnable implements Saveable, Runnable
{
    private int scoreboardUpdate = 20;
    private float dayTickFactor = 1;
    private int currentDay = 0;
    private int time = 23990;
    private int dayDurationCache = 24000;
    
    private final Game game;
    
    public GameRunnable(Game game)
    {
        this.game = game;
        updateDayDuration();
    }

    @Override
    public void run()
    {
        if(!game.getState().equals(GameState.STARTED))
            return;

        time++;
        /*
         * Set sun time
         */
        long worldTime = getExceptedWorldTime();
        for(World w : Bukkit.getWorlds())
        {
            if(!Fk.getInstance().getWorldManager().isAffected(w))
                continue;

            /*
             * Time skip
             * Dans le monde normal, si la diff n'est pas due au changement de jour. 32 correspond à une durée de jour de 750 ticks soit environ 45 sec.
             */
            if(w.getEnvironment().equals(World.Environment.NORMAL) && Math.abs(w.getTime() - worldTime) > 32 && time < dayDurationCache && !(currentDay == 0 && time < 20))
            {
                Fk.getInstance().getLogger().info("Ajustement de l'heure de la partie en fonction de l'heure du monde.");
                time = (int) (w.getTime() * dayTickFactor);
                worldTime = getExceptedWorldTime();
            }
            w.setTime(worldTime);
        }
        if(worldTime == 23000)
            Fk.broadcast(Messages.BROADCAST_SUN_WILL_RISE.getMessage());

        else if(time >= dayDurationCache)
        {
            currentDay++;
            time = 0;
            DayEvent dayEvent = new DayEvent(DayEvent.Type.NEW_DAY, currentDay, Messages.BROADCAST_DAY.getMessage().replace("%day%", String.valueOf(currentDay))); //EVENT
            Bukkit.getPluginManager().callEvent(dayEvent);
            Fk.broadcast(dayEvent.getMessage());
            if(Fk.getInstance().getConfig().getBoolean("enable-mcfunction-support", false))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "function fallenkingdom:newday");

            if(FkPI.getInstance().getRulesManager().getRule(Rule.DO_PAUSE_AFTER_DAY) && currentDay > 1)
                Fk.getInstance().getCommandManager().search(Pause.class).orElseThrow(RuntimeException::new).execute(Fk.getInstance(), Bukkit.getConsoleSender(), Collections.emptyList(), "fk");
            if(FkPI.getInstance().getRulesManager().getRule(Rule.PVP_CAP) == currentDay)
            {
                game.setPvpEnabled(true);
                DayEvent event = new DayEvent(DayEvent.Type.PVP_ENABLED, currentDay, Messages.BROADCAST_DAY_PVP.getMessage());
                Bukkit.getPluginManager().callEvent(event); //EVENT
                Fk.broadcast(event.getMessage(), FkSound.ENDERDRAGON_GROWL);
            }

            if(FkPI.getInstance().getRulesManager().getRule(Rule.TNT_CAP) == currentDay)
            {
                game.setAssaultsEnabled(true);
                DayEvent event = new DayEvent(DayEvent.Type.TNT_ENABLED, currentDay, Messages.BROADCAST_DAY_ASSAULT.getMessage());
                Bukkit.getPluginManager().callEvent(event); //EVENT
                Fk.broadcast(event.getMessage(), FkSound.ENDERDRAGON_GROWL);
            }

            if(FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_CAP) == currentDay)
            {
                game.setNetherEnabled(true);
                DayEvent event = new DayEvent(DayEvent.Type.NETHER_ENABLED, currentDay, Messages.BROADCAST_DAY_NETHER.getMessage());
                Bukkit.getPluginManager().callEvent(event); //EVENT
                Fk.getInstance().getPortalsManager().enablePortals();
                Fk.broadcast(event.getMessage(), FkSound.ENDERDRAGON_GROWL);
            }

            if(FkPI.getInstance().getRulesManager().getRule(Rule.END_CAP) == currentDay)
            {
                game.setEndEnabled(true);
                DayEvent event = new DayEvent(DayEvent.Type.END_ENABLED, currentDay, Messages.BROADCAST_DAY_END.getMessage());
                Bukkit.getPluginManager().callEvent(event); //EVENT
                Fk.getInstance().getPortalsManager().enablePortals();
                Fk.broadcast(event.getMessage(), FkSound.ENDERDRAGON_GROWL);
            }

            for(LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChestList())
                if(chest.getUnlockDay() == currentDay)
                    Fk.broadcast(Messages.BROADCAST_DAY_CHEST.getMessage().replace("%name%", chest.getName()).replace("%x%", String.valueOf(chest.getLocation().getBlockX())).replace("%y%", String.valueOf(chest.getLocation().getBlockY())).replace("%z%", String.valueOf(chest.getLocation().getBlockZ())), FkSound.ENDERMAN_TELEPORT);

            Fk.getInstance().getScoreboardManager().recreateAllScoreboards();
        }

        if(time % scoreboardUpdate == 0)
            Fk.getInstance().getScoreboardManager().refreshAllScoreboards(PlaceHolder.DAY, PlaceHolder.HOUR, PlaceHolder.MINUTE);

    }
    
    public void updateDayDuration()
    {
        float previousDayTickFactor = dayTickFactor;
        dayDurationCache = FkPI.getInstance().getRulesManager().getRule(Rule.DAY_DURATION);
        if(dayDurationCache < 1200)
        {
            FkPI.getInstance().getRulesManager().setRule(Rule.DAY_DURATION, 24000);
            dayDurationCache = 24000;
        }
        dayTickFactor = dayDurationCache / 24000f;
        scoreboardUpdate = dayDurationCache / 1200;
        time = (int) (time / previousDayTickFactor * dayTickFactor);
    }
    

    public long getExceptedWorldTime()
    {
        if (FkPI.getInstance().getRulesManager().getRule(Rule.ETERNAL_DAY))
            return 6000;
        else
            return dayDurationCache == 24000 ? time : (long) (time / dayTickFactor);
    }
    

    public String getFormattedTime()
    {
        return getHour()  + "h" + getMinute();
    }

    public String getHour()
    {
        if(currentDay == 0)
            return "--";
        int gameTime = (int) (time / dayTickFactor);
        int hours = gameTime / 1000 + 6;
        hours %= 24;
        return String.format("%02d", hours);
    }

    public String getMinute()
    {
        if(currentDay == 0)
            return "--";
        int gameTime = (int) (time / dayTickFactor);
        int minutes = (gameTime % 1000) * 60 / 1000;
        return String.format("%02d", minutes);
    }


    @Override
    public void load(ConfigurationSection config)
    {
        currentDay = config.getInt("Day");
        time = config.getInt("Time");
    }

    @Override
    public void save(ConfigurationSection config)
    {
        config.set("Day", currentDay);
        config.set("Time", time);
    }
}
