package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.commands.game.gamescommands.Pause;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.DayEvent;
import fr.devsylone.fkpi.api.event.GameEvent;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fkpi.lockedchests.LockedChest;
import fr.devsylone.fkpi.util.Saveable;

import java.util.Collections;

public class Game implements Saveable
{
	private GameState state = GameState.BEFORE_STARTING;
	private int day = 0;
	private int time = 23990;
	private int dayDuration = 24000;
	private int scoreboardUpdate = 20;
	private float dayTickFactor = 1;

	private boolean assault;
	private boolean pvp;
	private boolean nether;
	private boolean end;
	private int task;

	public enum GameState
	{
		BEFORE_STARTING,
		STARTING,
		STARTED,
		PAUSE
	}

	public GameState getState()
	{
		return state;
	}

	@SuppressWarnings("incomplete-switch")
	public boolean setState(GameState state)
	{
		if(this.state == state)
			return false;

		//Event creation
		switch(state)
		{
			case PAUSE:
				Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.PAUSE_EVENT));
				break;
			case STARTED:
				if(this.state == GameState.STARTING)
					Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.START_EVENT));
				else
					Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.RESUME_EVENT));
				break;
		}

		this.state = state;

		return true;
	}

	public void startTimer()
	{
		if(task != 0)
			throw new FkLightException("La partie est déjà commencée.");

		Fk.getInstance().getScoreboardManager().refreshAllScoreboards();

		if(!getState().equals(GameState.BEFORE_STARTING))
			Fk.getInstance().getTipsManager().cancelBroadcasts();

		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Fk.getInstance(), new Runnable()
		{
			public void run()
			{
				if(!getState().equals(GameState.STARTED))
					return;

				time++;
				/*
				 * Set sun time
				 */
				long worldTime = getExceptedWorldTime();
				for(World w : Bukkit.getWorlds())
				{
					/*
					 * Time skip
					 * Dans le monde normal, si la diff n'est pas due au changement de jour. 32 correspond à une durée de jour de 750 ticks soit environ 45 sec.
					 */
					if(w.getEnvironment().equals(World.Environment.NORMAL) && Math.abs(w.getTime() - worldTime) > 32 && time < dayDuration && !(day == 0 && time < 20))
					{
						Fk.getInstance().getLogger().info("Ajustement de l'heure de la partie en fonction de l'heure du monde.");
						time = (int) (w.getTime() * dayTickFactor);
						worldTime = getExceptedWorldTime();
					}
					w.setTime(worldTime);
				}
				if(worldTime == 23000)
					Fk.broadcast("" + ChatColor.GRAY + ChatColor.ITALIC + "Le soleil va bientôt se lever...");

				else if(time >= dayDuration)
				{
					day++;
					time = 0;
					Fk.broadcast("§bJour " + day);

					Bukkit.getPluginManager().callEvent(new DayEvent(DayEvent.Type.NEW_DAY, day)); //EVENT
					if(Fk.getInstance().getConfig().getBoolean("enable-mcfunction-support", false))
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"function fallenkingdom:newday");

					if(FkPI.getInstance().getRulesManager().getRule(Rule.DO_PAUSE_AFTER_DAY) && day > 1)
						Fk.getInstance().getCommandManager().search(Pause.class).get().execute(Fk.getInstance(), Bukkit.getConsoleSender(), Collections.emptyList(), "fk");
					if(FkPI.getInstance().getRulesManager().getRule(Rule.PVP_CAP) == day)
					{
						pvp = true;
						Fk.broadcast(Messages.BROADCAST_DAY_PVP.getMessage(), FkSound.ENDERDRAGON_GROWL);
						Bukkit.getPluginManager().callEvent(new DayEvent(DayEvent.Type.PVP_ENABLED, day)); //EVENT
					}

					if(FkPI.getInstance().getRulesManager().getRule(Rule.TNT_CAP) == day)
					{
						assault = true;
						Fk.broadcast(Messages.BROADCAST_DAY_ASSAULT.getMessage(), FkSound.ENDERDRAGON_GROWL);
						Bukkit.getPluginManager().callEvent(new DayEvent(DayEvent.Type.TNT_ENABLED, day)); //EVENT
					}

					if(FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_CAP) == day)
					{
						nether = true;
						Fk.broadcast(Messages.BROADCAST_DAY_NETHER.getMessage(), FkSound.ENDERDRAGON_GROWL);
						Fk.getInstance().getPortalsManager().enablePortals();
						Bukkit.getPluginManager().callEvent(new DayEvent(DayEvent.Type.NETHER_ENABLED, day)); //EVENT
					}

					if(FkPI.getInstance().getRulesManager().getRule(Rule.END_CAP) == day)
					{
						end = true;
						Fk.broadcast(Messages.BROADCAST_DAY_END.getMessage(), FkSound.ENDERDRAGON_GROWL);
						Bukkit.getPluginManager().callEvent(new DayEvent(DayEvent.Type.END_ENABLED, day)); //EVENT
					}

					for(LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChestList())
						if(chest.getUnlockDay() == day)
							Fk.broadcast(Messages.BROADCAST_DAY_CHEST.getMessage()
									.replace("%name%", chest.getName())
									.replace("%x%", String.valueOf(chest.getLocation().getBlockX()))
									.replace("%y%", String.valueOf(chest.getLocation().getBlockY()))
									.replace("%z%", String.valueOf(chest.getLocation().getBlockZ()))
								, FkSound.ENDERMAN_TELEPORT);

					Fk.getInstance().getScoreboardManager().recreateAllScoreboards();
				}

				if(time % scoreboardUpdate == 0)
					Fk.getInstance().getScoreboardManager().refreshAllScoreboards(PlaceHolder.DAY, PlaceHolder.HOUR, PlaceHolder.MINUTE);

			}
		}, 1L, 1L);
	}

	public void stop()
	{
		Bukkit.getScheduler().cancelTask(task);
		task = 0;
		setState(GameState.BEFORE_STARTING);

		day = 0;
		time = 23990;
		assault = false;
		pvp = false;
		nether = false;
		end = false;
		Fk.getInstance().getScoreboardManager().refreshAllScoreboards();
	}

	public int getDays()
	{
		return day;
	}

	public int getTime()
	{
		return time;
	}

	public long getExceptedWorldTime()
	{
		if (FkPI.getInstance().getRulesManager().getRule(Rule.ETERNAL_DAY))
			return 6000;
		else
			return dayDuration == 24000 ? time : (long) (time / dayTickFactor);
	}

	public void updateDayDuration()
	{
		float previousDayTickFactor = dayTickFactor;
		dayDuration = FkPI.getInstance().getRulesManager().getRule(Rule.DAY_DURATION);
		if (dayDuration < 1200) {
			FkPI.getInstance().getRulesManager().setRule(Rule.DAY_DURATION, 24000);
			dayDuration = 24000;
		}
		dayTickFactor = dayDuration/24000f;
		scoreboardUpdate = dayDuration/1200;
		time = (int) (time/previousDayTickFactor * dayTickFactor);
	}

	public String getFormattedTime()
	{
		int gameTime = (int) (time / dayTickFactor);
		int hours = gameTime / 1000 + 6;
		hours %= 24;
		int minutes = (gameTime % 1000) * 60 / 1000;
		String mm = "0" + minutes;
		mm = mm.substring(mm.length() - 2);
		return hours + "h" + mm;
	}

	public String getHour()
	{
		int gameTime = (int) (time / dayTickFactor);
		int hours = gameTime / 1000 + 6;
		hours %= 24;
		return String.valueOf(hours);
	}

	public String getMinute()
	{
		int gameTime = (int) (time / dayTickFactor);
		int minutes = (gameTime % 1000) * 60 / 1000;
		String mm = "0" + minutes;
		mm = mm.substring(mm.length() - 2);
		return mm;
	}

	public boolean isAssaultsEnabled()
	{
		return assault;
	}

	public boolean isPvpEnabled()
	{
		return pvp;
	}

	public boolean isNetherEnabled()
	{
		return nether;
	}

	public boolean isEndEnabled()
	{
		return end;
	}

	public void load(ConfigurationSection config)
	{
		state = GameState.valueOf(config.getString("State"));
		day = config.getInt("Day");
		time = config.getInt("Time");

		pvp = FkPI.getInstance().getRulesManager().getRule(Rule.PVP_CAP) <= day;
		assault = FkPI.getInstance().getRulesManager().getRule(Rule.TNT_CAP) <= day;
		nether = FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_CAP) <= day;
		end = FkPI.getInstance().getRulesManager().getRule(Rule.END_CAP) <= day;
		startTimer();
		updateDayDuration();

		if(state.equals(GameState.STARTING))
		{
			state = GameState.BEFORE_STARTING;
			start();
		}

		else if(state.equals(GameState.PAUSE) && FkPI.getInstance().getRulesManager().getRule(Rule.DEEP_PAUSE))
		{
			Fk.getInstance().getDeepPauseManager().removeAIs();
			Fk.getInstance().getDeepPauseManager().protectDespawnItems();
		}
	}

	public void save(ConfigurationSection config)
	{
		config.set("State", state.name());
		config.set("Day", day);
		config.set("Time", time);
	}

	public void start()
	{
		if(!state.equals(GameState.BEFORE_STARTING))
			throw new FkLightException(Messages.CMD_ERROR_GAME_ALREADY_STARTED);

		updateDayDuration();
		setState(GameState.STARTING);
		long time = 0;

		broadcastStartIn(30);

		time += 5;
		delayedRunnable(() -> Fk.broadcast("Pour connaître les règles : §e/fk rules list"), time);
		time += 5;
		delayedRunnable(() -> broadcastStartIn(20), time);
		time += 5;
		delayedRunnable(() -> Fk.broadcast("Pour connaître la liste des équipes, leurs joueurs et les coordonnées de leur base : §e/fk team list"), time);
		time += 5;
		delayedRunnable(() -> broadcastStartIn(10), time);
		time += 5;

		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
			for(int i = 5; i > 0; i--)
			{
				final int a = i;
				Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> broadcastTpIn(6 - a), i * 20);
			}
		}, time * 20L);

		time += 6;

		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()) != null && Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()).getBase() != null)
				{
					p.teleport(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()).getBase().getTpPoint());

					p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 4));
					p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 30, 4));
					p.setGameMode(GameMode.SURVIVAL);
					p.setHealth(20);
					p.setFoodLevel(20);
					p.setSaturation(20);
					p.setFlying(false);
					Fk.getInstance().getStarterInventoryManager().applyStarterInv(p);
				}

				p.playSound(p.getLocation(), FkSound.EXPLODE.bukkitSound(), 1, 1);
			}

			for(World w : Bukkit.getWorlds())
				w.setTime(FkPI.getInstance().getRulesManager().getRule(Rule.ETERNAL_DAY) ? 6000L : 23990L);

			Fk.broadcast(Messages.BROADCAST_START.getMessage());
			setState(GameState.STARTED);
			startTimer();
		}, time * 20L);
	}

	private void delayedRunnable(Runnable runnable, long delay)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), runnable, delay * 20L);
	}

	private void broadcastStartIn(int time)
	{
		Fk.broadcast("La partie démarre dans §c" + time + "§r seconde(s)");
		for(Player p : Bukkit.getOnlinePlayers())
			p.playSound(p.getLocation(), FkSound.NOTE_PLING.bukkitSound(), 1, 1);
	}

	private void broadcastTpIn(int time)
	{
		Fk.broadcast("Vous serez téléporté dans §c" + time + "§r seconde(s)");
		for(Player p : Bukkit.getOnlinePlayers())
			p.playSound(p.getLocation(), FkSound.NOTE_BASS.bukkitSound(), 1, 1);
	}
}
