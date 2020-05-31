package fr.devsylone.fallenkingdom.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.event.GameEvent;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.Saveable;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Game implements Saveable
{
	private GameState state = GameState.BEFORE_STARTING;

	@Setter private boolean assaultsEnabled;
	@Setter private boolean pvpEnabled;
	@Setter private boolean netherEnabled;
	@Setter private boolean endEnabled;
    private GameRunnable gameRunnable = new GameRunnable(this);
    private BukkitTask gameTask = null;

	public enum GameState
	{
		BEFORE_STARTING,
		STARTING,
		STARTED,
		PAUSE
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

	public void stop()
	{
	    if(gameTask != null)
	        gameTask.cancel();
	    gameTask = null;
	    gameRunnable = new GameRunnable(this);
	    
		setState(GameState.BEFORE_STARTING);

		assaultsEnabled = false;
		pvpEnabled = false;
		netherEnabled = false;
		endEnabled = false;
	}

	public void load(ConfigurationSection config)
	{
		state = GameState.valueOf(config.getString("State"));
		gameRunnable.load(config.getConfigurationSection("GameRunnable"));
        int currentDay = gameRunnable.getCurrentDay();
		pvpEnabled = FkPI.getInstance().getRulesManager().getRule(Rule.PVP_CAP) <= currentDay;
		assaultsEnabled = FkPI.getInstance().getRulesManager().getRule(Rule.TNT_CAP) <= currentDay;
		netherEnabled = FkPI.getInstance().getRulesManager().getRule(Rule.NETHER_CAP) <= currentDay;
		endEnabled = FkPI.getInstance().getRulesManager().getRule(Rule.END_CAP) <= currentDay;
		gameRunnable.updateDayDuration();

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
            case STARTED:
                if(gameTask != null)
                    gameTask.cancel();
                gameRunnable = new GameRunnable(this);
                gameTask = Bukkit.getScheduler().runTaskTimer(Fk.getInstance(), gameRunnable, 1l, 1l);
            case BEFORE_STARTING:
                break;
            default:
                break;
		}
	}

	public void save(ConfigurationSection config)
	{
		config.set("State", state.name());
        gameRunnable.save(config.createSection("GameRunnable"));
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

            gameRunnable.updateDayDuration();
			for(World w : Bukkit.getWorlds())
				w.setTime(FkPI.getInstance().getRulesManager().getRule(Rule.ETERNAL_DAY) ? 6000L : 23990L);

            Fk.broadcast(Messages.BROADCAST_START.getMessage());
            setState(GameState.STARTED);
            if(gameTask != null)
                gameTask.cancel();
            gameRunnable = new GameRunnable(this);
            gameTask = Bukkit.getScheduler().runTaskTimer(Fk.getInstance(), gameRunnable, 1l, 1l);
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
		Fk.broadcast(Messages.BROADCAST_PREGAME_TP.getMessage().replace("%time%", String.valueOf(time)));
		for(Player p : Bukkit.getOnlinePlayers())
			p.playSound(p.getLocation(), FkSound.NOTE_BASS.bukkitSound(), 1, 1);
	}
}
