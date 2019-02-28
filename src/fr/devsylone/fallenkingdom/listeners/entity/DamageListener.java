package fr.devsylone.fallenkingdom.listeners.entity;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.players.FkPlayer.PlayerState;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;
import fr.devsylone.fkpi.rules.ChargedCreepers;

public class DamageListener implements Listener
{
	@EventHandler
	public void damage(EntityDamageEvent e)
	{
		if(Fk.getInstance().getGame().getState().equals(GameState.PAUSE))
			e.setCancelled(true);
	}

	@EventHandler
	public void creeperDeath(EntityDeathEvent e)
	{
		ChargedCreepers rule = (ChargedCreepers) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("ChargedCreepers");
		if(e.getEntity() instanceof Creeper && ((Creeper) e.getEntity()).isPowered() && (new Random().nextInt(100) <= rule.getDrop()))
			e.getDrops().add(new ItemStack(Material.TNT, rule.getTntAmount()));
	}

	@EventHandler
	public void dead(PlayerDeathEvent e)
	{
		e.setDeathMessage(ChatUtils.PREFIX + e.getDeathMessage());

		/*
		 * Si le killer a une team
		 */
		if(e.getEntity().getKiller() != null && Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getEntity().getKiller().getName()) != null)
		{
			/*
			 * Sound
			 */
			for(Player p : Bukkit.getOnlinePlayers())
				p.playSound(p.getLocation(), FkSound.WITHER_SPAWN.bukkitSound(), 1, 1);

			/*
			 * Replace color killer
			 */
			e.setDeathMessage(e.getDeathMessage().replaceAll(e.getEntity().getKiller().getName(), e.getEntity().getKiller().getDisplayName()));

			/*
			 * Add kill Si killer != dead
			*/
			if(e.getEntity().getKiller() != null && e.getEntity().getKiller().getName() != e.getEntity().getName())
				Fk.getInstance().getPlayerManager().getPlayer(e.getEntity().getKiller().getName()).addKill();
		}
		/*
		 * Replace color killed
		 */
		if(Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getEntity().getName()) != null)
			e.setDeathMessage(e.getDeathMessage().replaceAll(e.getEntity().getName(), e.getEntity().getDisplayName()));

		/*
		 * Si tué ou tueur pas de team pas de deathlimit
		 */
		if(Fk.getInstance().getGame().getState().equals(GameState.BEFORE_STARTING) || Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getEntity().getName()) == null || (e.getEntity().getKiller() != null && Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getEntity().getKiller().getName()) == null))
			return;

		/*
		 * Add death
		 */
		Fk.getInstance().getPlayerManager().getPlayer(e.getEntity().getName()).addDeath();

		/*
		 * DeathLimit > 0
		 */
		if((int) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DeathLimit").getValue() > 0)
			if(Fk.getInstance().getPlayerManager().getPlayer(e.getEntity().getName()).getDeaths() >= (int) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DeathLimit").getValue())
			{
				/*
				 * Elimination
				 */

				Fk.broadcast(e.getEntity().getDisplayName() + " §4est éliminé !");
				e.getEntity().setGameMode(GameMode.SPECTATOR);

				for(Player p : Bukkit.getOnlinePlayers())
					p.playSound(p.getLocation(), FkSound.ENDERDRAGON_DEATH.bukkitSound(), 1, 1);
			}
			/*
			 * Info nbre de vie
			 */
			else
				e.getEntity().sendMessage("§cVous avez déjà perdu " + Fk.getInstance().getPlayerManager().getPlayer(e.getEntity().getName()).getDeaths() + "/" + Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DeathLimit").getValue() + " vie(s)");

		FkPlayer fkP = Fk.getInstance().getPlayerManager().getPlayer(e.getEntity().getName());
		if(fkP.getState() == PlayerState.EDITING_SCOREBOARD)
			fkP.getSbDisplayer().exit();

		Fk.getInstance().getScoreboardManager().refreshAllScoreboards(PlaceHolder.DEATHS);
		Fk.getInstance().getScoreboardManager().refreshAllScoreboards(PlaceHolder.KILLS);
	}
}
