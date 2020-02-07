package fr.devsylone.fallenkingdom.game;

import java.text.SimpleDateFormat;
import java.util.Date;

//import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
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

public class Game implements Saveable
{
	private GameState state;
	private int day;
	private int time;

	private boolean assault;
	private boolean pvp;
	private boolean nether;
	private boolean end;
	private int task;

	public enum GameState
	{
		BEFORE_STARTING(),
		STARTING(),
		STARTED(),
		PAUSE();
	}

	public Game()
	{
		state = GameState.BEFORE_STARTING;
		day = 0;
		time = 23999;
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
				Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.PAUSE_EVENT, day));
				break;
			case STARTED:
				if(this.state == GameState.STARTING)
					Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.START_EVENT, day));
				else
					Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.RESUME_EVENT, day));
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

		if(getState().equals(GameState.STARTED))
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
				for(World w : Bukkit.getWorlds())
					w.setTime((boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("EternalDay").getValue() ? 6000l : time);

				if(time == 23000)
					Fk.broadcast("" + ChatColor.GRAY + ChatColor.ITALIC + "Le soleil va bientôt se lever...");

				else if(time == 24000)
				{

					for(World w : Bukkit.getWorlds())
						w.setTime(0l);

					day++;
					time = 0;
					Fk.broadcast("§bJour " + day);

					Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.NEW_DAY, day)); //EVENT

					if((boolean) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DoPauseAfterDay").getValue() && day > 1)
						try
						{
							Fk.getInstance().getCommandManager().getCommand("game pause").execute(null, null, new String[0]);
						}catch(Exception ex)
						{
							Fk.broadcast("§4 Une erreur est survenue lors de la mise en pause. Merci de signaler ce bug.");
							ex.printStackTrace();
						}
					if((int) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("PvpCap").getValue() == day)
					{
						pvp = true;
						Fk.broadcast("§cLe pvp est désormais actif !", FkSound.ENDERDRAGON_GROWL);
						Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.PVP_ENABLED, day)); //EVENT
					}

					if((int) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("TntCap").getValue() == day)
					{
						assault = true;
						Fk.broadcast("§cLes assauts sont désormais actifs !", FkSound.ENDERDRAGON_GROWL);
						Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.TNT_ENABLED, day)); //EVENT
					}

					if((int) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("NetherCap").getValue() == day)
					{
						nether = true;
						Fk.broadcast("§cLe nether est désormais ouvert !", FkSound.ENDERDRAGON_GROWL);
						Fk.getInstance().getPortalsManager().enablePortals();
						Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.NETHER_ENABLED, day)); //EVENT
					}

					if((int) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("EndCap").getValue() == day)
					{
						end = true;
						Fk.broadcast("§cL'end est désormais ouvert !", FkSound.ENDERDRAGON_GROWL);
						Bukkit.getPluginManager().callEvent(new GameEvent(GameEvent.Type.END_ENABLED, day)); //EVENT
					}

					for(LockedChest chest : Fk.getInstance().getFkPI().getLockedChestsManager().getChestList())
						if(chest.getUnlockDay() == day)
							Fk.broadcast("§cLe coffre §5" + chest.getName() + "§c est crochetable en x:" + chest.getLocation().getBlockX() + " y:" + chest.getLocation().getBlockY() + " z:" + chest.getLocation().getBlockZ() + " !", FkSound.ENDERMAN_TELEPORT);

					Fk.getInstance().getScoreboardManager().recreateAllScoreboards();
				}

				if(time % 20 == 0)
					Fk.getInstance().getScoreboardManager().refreshAllScoreboards(PlaceHolder.DAY, PlaceHolder.HOUR, PlaceHolder.MINUTE);

			}
		}, 1l, 1l);
	}

	public void stop()
	{
		Bukkit.getScheduler().cancelTask(task);
		task = 0;
		setState(GameState.BEFORE_STARTING);

		day = 0;
		time = 23999;
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

	public String getFormattedTime()
	{
		Date time = new Date((int) ((Fk.getInstance().getGame().getTime() * 3.6 + 5 * 60 * 60) * 1000));

		String h = new SimpleDateFormat("HH").format(time);
		h = (String) (Integer.parseInt(h) > 24 ? "0" + (Integer.parseInt(h) - 24) : h);

		String m = new SimpleDateFormat("mm").format(time);

		return h + "h" + m;
	}

	public String getHour()
	{
		Date time = new Date((int) ((Fk.getInstance().getGame().getTime() * 3.6 + 5 * 60 * 60) * 1000));

		String h = new SimpleDateFormat("HH").format(time);
		h = (String) (Integer.parseInt(h) > 24 ? "0" + (Integer.parseInt(h) - 24) : h);
		return h;
	}

	public String getMinute()
	{
		Date time = new Date((int) ((Fk.getInstance().getGame().getTime() * 3.6 + 5 * 60 * 60) * 1000));

		String m = new SimpleDateFormat("mm").format(time);
		return m;
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

		pvp = (int) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("PvpCap").getValue() <= day;
		assault = (int) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("TntCap").getValue() <= day;
		nether = (int) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("NetherCap").getValue() <= day;
		end = (int) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("EndCap").getValue() <= day;
		startTimer();

		if(state.equals(GameState.STARTING))
		{
			state = GameState.BEFORE_STARTING;
			start();
		}

		else if(state.equals(GameState.PAUSE))
			try
			{
				Fk.getInstance().getCommandManager().getCommand("game pause").execute(null, null, new String[0]);
			}catch(Exception ex)
			{
				Fk.broadcast("§4Une erreur est survenue lors de la mise en pause. Merci de signaler ce bug.");
				ex.printStackTrace();
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
			throw new FkLightException("La partie est déjà commencée.");

		setState(GameState.STARTING);
		long time = 0;

		broadcastStartIn(30);

		time += 5;

		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				Fk.broadcast("Pour connaître les règles : §e/fk rules list");
			}
		}, time * 20l);

		time += 5;

		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				broadcastStartIn(20);
			}
		}, time * 20l);

		time += 5;

		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				Fk.broadcast("Pour connaître la liste des équipes, leurs joueurs et les coordonées de leur base : §e/fk team list");
			}
		}, time * 20l);

		time += 5;

		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				broadcastStartIn(10);
			}
		}, time * 20l);

		time += 5;

		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				for(int i = 5; i > 0; i--)
				{
					final int a = i;
					Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
					{
						@Override
						public void run()
						{
							broadcastTpIn(6 - a);
						}
					}, i * 20);
				}
			}
		}, time * 20l);

		time += 6;

		Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				for(Player p : Bukkit.getOnlinePlayers())
				{
					if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()) != null && Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()).getBase() != null)
					{
						p.teleport(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(p.getName()).getBase().getTpPoint());

						p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 4));
						p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 30, 4));
						p.setHealth(20);
						p.setFoodLevel(20);
						p.setSaturation(20);
						p.setFlying(false);
						Fk.getInstance().getStarterInventoryManager().applyStarterInv(p);
					}

					/*if(Bukkit.getBukkitVersion().contains("1.8"))
						for(Achievement a : Achievement.values())
							if(p.hasAchievement(a))
								p.removeAchievement(a);*/

					p.getWorld().setDifficulty(Difficulty.NORMAL);
					p.setGameMode(GameMode.SURVIVAL);
					p.playSound(p.getLocation(), FkSound.EXPLODE.bukkitSound(), 1, 1);
				}

				Fk.broadcast("§2La partie commence, bonne chance à tous !");
				
				setState(GameState.STARTED);
				if(task == 0)
					startTimer();
			}
		}, time * 20l);
	}

	private void broadcastStartIn(int time)
	{
		Fk.broadcast("La partie démarre dans §c" + time + "§r seconde(s)");
		@SuppressWarnings("unused")
		boolean minecraft18;
		for(Player p : Bukkit.getOnlinePlayers()) {
			
			p.playSound(p.getLocation(), FkSound.NOTE_PLING.bukkitSound(), 1, 1);
			
			if(Bukkit.getServer().getClass().getPackage().getName().contains("1_8"))
			{continue;}
		
			else
			{
			Fk.getInstance().getPacketManager().sendTitle(p,"§6\u2694 La partie va commencer \u2694", "§a" + Integer.toString(time) + "§a secondes...", 25, 100, 25);
			}
		}
	}

	private void broadcastTpIn(int time)
	{
		Fk.broadcast("Vous serez téléporté dans §c" + time + "§r seconde(s)");
		for(Player p : Bukkit.getOnlinePlayers())
			p.playSound(p.getLocation(), FkSound.NOTE_BASS.bukkitSound(), 1, 1);
	}
}
