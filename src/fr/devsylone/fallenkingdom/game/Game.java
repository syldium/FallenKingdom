package fr.devsylone.fallenkingdom.game;

import fr.devsylone.fallenkingdom.display.GlobalDisplayService;
import fr.devsylone.fallenkingdom.display.tick.CycleTickFormatter;
import fr.devsylone.fallenkingdom.display.tick.TickFormatter;
import fr.devsylone.fkpi.api.event.DayEvent;
import fr.devsylone.fkpi.api.event.RuleChangeEvent;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static fr.devsylone.fallenkingdom.display.tick.CycleTickFormatter.TICKS_PER_DAY_NIGHT_CYCLE;
import static fr.devsylone.fallenkingdom.utils.ConfigHelper.enumValueOf;

public class Game implements Saveable
{
	private static final List<Rule<?>> OBSERVED_RULES = Arrays.asList(Rule.PVP_CAP, Rule.TNT_CAP, Rule.NETHER_CAP, Rule.END_CAP);

	@Getter protected GameState state = GameState.BEFORE_STARTING;
	@Getter protected int day = 0;
	@Getter protected int time = FkPI.getInstance().getRulesManager().getRule(Rule.DAY_DURATION) - 10;
	@Getter protected TickFormatter timeFormat = null; // Toujours non null une fois les données chargées

	protected GameRunnable task = null;

	@Getter protected boolean assaultsEnabled = false;
	@Getter protected boolean pvpEnabled = false;
	@Getter protected boolean netherEnabled = false;
	@Getter protected boolean endEnabled = false;

	public enum GameState
	{
		BEFORE_STARTING,
		STARTING,
		STARTED,
		PAUSE;

		private final String name;

		GameState() {
			this.name = this.name().toLowerCase(Locale.ROOT);
		}

		public String asString() {
			return this.name;
		}
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
		if(task != null)
			throw new IllegalStateException("Main timer already running");

		if (hasStarted())
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

    public <T> void onRuleChange(RuleChangeEvent<T> event) {
        if (updateCap(event.getRule()) != null) {
            playEventSound();
			Fk.getInstance().getDisplayService().updateAll();
        }
    }

	@Override
	public void load(ConfigurationSection config)
	{
		final int savedDay = config.getInt("Day");
		final int savedTime = config.getInt("Time");
		if (savedDay < 0) {
			Fk.getInstance().getLogger().severe("Invalid game day read from config: " + savedDay);
		}
		if (savedTime < 0) {
			Fk.getInstance().getLogger().severe("Invalid game time read from config: " + savedTime);
		}
		day = Math.max(0, savedDay);
		time = Math.max(0, savedTime);
		state = enumValueOf(GameState.class, config.getString("State"), day > 1 ? GameState.STARTED : GameState.BEFORE_STARTING);
		if (isPreStart() && day != 0) {
			Fk.getInstance().getLogger().severe("The game has not started and is on day " + savedDay + " at the same time.");
			state = GameState.STARTED;
		}

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

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("State", state.name());
		config.set("Day", day);
		config.set("Time", time);
	}

	public void start()
	{
		if(hasStarted())
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

		Bukkit.getScheduler().runTaskLater(Fk.getInstance(), () -> {
			for(int i = 5; i > 0; i--)
			{
				final int a = i;
				Bukkit.getScheduler().runTaskLater(Fk.getInstance(), () -> broadcastTpIn(6 - a), i * 20);
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
						w.setFullTime(getExceptedWorldTime());
				}

				Fk.broadcast(Messages.BROADCAST_START.getMessage());
				setState(GameState.STARTED);
				startTimer();
			}, delayTeleportByTeam + 5);
        }, time * 20L);
	}

	private void delayedRunnable(Runnable runnable, long delay)
	{
		Bukkit.getScheduler().runTaskLater(Fk.getInstance(), runnable, delay * 20L);
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
			return 6000L;
		else
			return timeFormat.worldTime(day, time);
	}

	public void updateDayDuration()
	{
		int dayDuration = FkPI.getInstance().getRulesManager().getRule(Rule.DAY_DURATION);
		if (dayDuration < 1200) {
			FkPI.getInstance().getRulesManager().setRule(Rule.DAY_DURATION, TICKS_PER_DAY_NIGHT_CYCLE);
			dayDuration = TICKS_PER_DAY_NIGHT_CYCLE;
		}
		if (timeFormat == null) {
			timeFormat = new CycleTickFormatter(dayDuration, false, false, 6);
			return;
		}
		long previousTime = timeFormat.worldTime(day, time);
		timeFormat = timeFormat.withDayDuration(dayDuration);
		time = timeFormat.timeFromWorld(previousTime);
		day = timeFormat.dayFromWorld(previousTime);
	}

	public void updateDayDuration(@NotNull GlobalDisplayService displayService)
	{
		timeFormat = displayService.configureTickFormatter(timeFormat.dayDuration());
	}

	public String getFormattedTime()
	{
		return getHour() + 'h' + getMinute();
	}

	public String getHour()
	{
		if(day == 0)
			return "--";
		return timeFormat.formatHours(time);
	}

	public String getMinute()
	{
		if(day == 0)
			return "--";
		return timeFormat.formatMinutes(time);
	}

	public boolean isPreStart()
	{
		return state == GameState.BEFORE_STARTING;
	}

	public boolean hasStarted()
	{
		return state != GameState.BEFORE_STARTING;
	}

	public boolean isPaused()
	{
		return state == GameState.PAUSE;
	}

	<T> @Nullable DayEvent updateCap(Rule<T> cap) {
		DayEvent event = null;
		if (Rule.PVP_CAP.equals(cap)) {
			if (FkPI.getInstance().getRulesManager().getRule(Rule.PVP_CAP) == day) {
				pvpEnabled = true;
				event = new DayEvent(DayEvent.Type.PVP_ENABLED, day, Messages.BROADCAST_DAY_PVP.getMessage());
			}
		} else if (Rule.TNT_CAP.equals(cap)) {
			if (FkPI.getInstance().getRulesManager().getRule(Rule.TNT_CAP) == day) {
				assaultsEnabled = true;
				event = new DayEvent(DayEvent.Type.TNT_ENABLED, day, Messages.BROADCAST_DAY_ASSAULT.getMessage());
			}
		} else if (Rule.NETHER_CAP.equals(cap)) {
			if (FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_CAP) == day) {
				netherEnabled = true;
				event = new DayEvent(DayEvent.Type.NETHER_ENABLED, day, Messages.BROADCAST_DAY_NETHER.getMessage());
				Fk.getInstance().getPortalsManager().enablePortals();
			}
		} else if (Rule.END_CAP.equals(cap)) {
			if (FkPI.getInstance().getRulesManager().getRule(Rule.END_CAP) == day) {
				endEnabled = true;
				event = new DayEvent(DayEvent.Type.END_ENABLED, day, Messages.BROADCAST_DAY_END.getMessage());
			}
		}
		if (event != null) {
			Bukkit.getPluginManager().callEvent(event);
			if (event.getMessage() != null) {
				Fk.broadcast(event.getMessage());
			}
		}
		return event;
	}

	boolean updateCaps() {
		boolean changed = false;
		for (Rule<?> rule : OBSERVED_RULES) {
			changed |= updateCap(rule) != null;
		}
		if (changed) {
			playEventSound();
		}
		return changed;
	}

	private void playEventSound() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (Fk.getInstance().getWorldManager().isAffected(player.getWorld())) {
				Fk.getInstance().getDisplayService().playEventSound(player);
			}
		}
	}
}
