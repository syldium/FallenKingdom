package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.GameEvent;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.Saveable;
import lombok.Getter;

public class Game implements Saveable
{
	@Getter protected GameState state = GameState.BEFORE_STARTING;
	@Getter protected int day = 0;
	@Getter protected int time = FkPI.getInstance().getRulesManager().getRule(Rule.DAY_DURATION) - 10;

	protected GameRunnable task = null;
	protected int dayDurationCache = 24000;
	protected int scoreboardUpdate = 20;
	protected float dayTickFactor = 1;

	@Getter protected boolean assaultsEnabled = false;
	@Getter protected boolean pvpEnabled = false;
	@Getter protected boolean netherEnabled = false;
	@Getter protected boolean endEnabled = false;

	public enum GameState
	{
		BEFORE_STARTING,
		STARTING,
		STARTED,
		PAUSE
	}

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
		}

		this.state = state;

		return true;
	}

	public void startTimer()
	{
		if(task != null && !task.isCancelled())
			throw new IllegalStateException("Main timer already running");

		if(!state.equals(GameState.BEFORE_STARTING))
			Fk.getInstance().getTipsManager().cancelBroadcasts();

		task = new GameRunnable(this);
		task.runTaskTimer(Fk.getInstance(), 1L, 1L);
	}

	public void stopTimer()
	{
		if (task != null)
			task.cancel();
		task = null;
	}

	public void stop()
	{
	    stopTimer();
		setState(GameState.BEFORE_STARTING);

		day = 0;
		time = FkPI.getInstance().getRulesManager().getRule(Rule.DAY_DURATION) - 10;
		assaultsEnabled = false;
		pvpEnabled = false;
		netherEnabled = false;
		endEnabled = false;
	}

	public void load(ConfigurationSection config)
	{
		state = GameState.valueOf(config.getString("State"));
		day = config.getInt("Day");
		time = config.getInt("Time");

		pvpEnabled = FkPI.getInstance().getRulesManager().getRule(Rule.PVP_CAP) <= day;
		assaultsEnabled = FkPI.getInstance().getRulesManager().getRule(Rule.TNT_CAP) <= day;
		netherEnabled = FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_CAP) <= day;
		endEnabled = FkPI.getInstance().getRulesManager().getRule(Rule.END_CAP) <= day;
		updateDayDuration();

		switch (state) {
			case STARTING:
				state = GameState.BEFORE_STARTING;
				start();
				break;
			case PAUSE:
				if (FkPI.getInstance().getRulesManager().getRule(Rule.DEEP_PAUSE)) {
					Fk.getInstance().getDeepPauseManager().removeAIs();
					Fk.getInstance().getDeepPauseManager().protectDespawnItems();
				}
				break;
			case STARTED:
				startTimer();
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

		setState(GameState.STARTING);
		int time = 0;

		broadcastStartIn(30);

		time += 5;
		delayedRunnable(() -> Fk.broadcast(Messages.BROADCAST_PREGAME_RULES.getMessage()), time);
		time += 5;
		delayedRunnable(() -> broadcastStartIn(20), time);
		time += 5;
		delayedRunnable(() -> Fk.broadcast(Messages.BROADCAST_PREGAME_TEAMS.getMessage()), time);
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

		Bukkit.getScheduler().runTaskLater(Fk.getInstance(), () -> {
			long delayTeleportByTeam = 0;
			for (Team team : FkPI.getInstance().getTeamManager().getTeams()) {
				Bukkit.getScheduler().runTaskLater(Fk.getInstance(), new TeleportTask(team), delayTeleportByTeam);
				delayTeleportByTeam += team.getPlayers().size() * 4;
			}

			Bukkit.getScheduler().runTaskLater(Fk.getInstance(), () -> {
				updateDayDuration();
				for(World w : Bukkit.getWorlds()) {
					if (Fk.getInstance().getWorldManager().isAffected(w))
						w.setTime(getExceptedWorldTime());
				}

				Fk.broadcast(Messages.BROADCAST_START.getMessage());
				setState(GameState.STARTED);
				startTimer();
			}, delayTeleportByTeam + 5);
        }, time * 20L);
	}

	private void delayedRunnable(Runnable runnable, long delay)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), runnable, delay * 20L);
	}

	private void broadcastStartIn(int time)
	{
		Fk.broadcast(Messages.BROADCAST_PREGAME_START.getMessage().replace("%time%", String.valueOf(time)));
		for(Player p : Bukkit.getOnlinePlayers())
			p.playSound(p.getLocation(), FkSound.NOTE_PLING.bukkitSound(), 1, 1);
	}

	private void broadcastTpIn(int time)
	{
		Fk.broadcast(Messages.BROADCAST_PREGAME_TP.getMessage()
				.replace("%time%", String.valueOf(time))
				.replace("%unit%", Messages.Unit.SECONDS.tl(time))
		);
		for(Player p : Bukkit.getOnlinePlayers())
			p.playSound(p.getLocation(), FkSound.NOTE_BASS.bukkitSound(), 1, 1);
	}

	public long getExceptedWorldTime()
	{
		if (FkPI.getInstance().getRulesManager().getRule(Rule.ETERNAL_DAY))
			return 6000;
		else
			return dayDurationCache == 24000 ? time : (long) (time / dayTickFactor);
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

	public String getFormattedTime()
	{
		return getHour()  + "h" + getMinute();
	}

	public String getHour()
	{
		if(day == 0)
			return "--";
		int gameTime = (int) (time / dayTickFactor);
		int hours = gameTime / 1000 + 6;
		hours %= 24;
		return String.format("%02d", hours);
	}

	public String getMinute()
	{
		if(day == 0)
			return "--";
		int gameTime = (int) (time / dayTickFactor);
		int minutes = (gameTime % 1000) * 60 / 1000;
		return String.format("%02d", minutes);
	}
}
