package fr.devsylone.fallenkingdom.listeners.entity;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.players.FkPlayer.PlayerState;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Environment;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.ChargedCreepers;
import fr.devsylone.fkpi.rules.Rule;
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

import java.util.Random;

public class DamageListener implements Listener
{
    @EventHandler
    public void damage(EntityDamageEvent e)
    {
        if(!Fk.getInstance().getWorldManager().isAffected(e.getEntity().getWorld()))
            return;
        if(Fk.getInstance().getGame().isPaused() && e.getCause() != EntityDamageEvent.DamageCause.VOID)
            e.setCancelled(true);
    }

    @EventHandler
    public void creeperDeath(EntityDeathEvent e)
    {
        if(!Fk.getInstance().getWorldManager().isAffected(e.getEntity().getWorld()))
            return;
        ChargedCreepers rule = FkPI.getInstance().getRulesManager().getRule(Rule.CHARGED_CREEPERS);
        if(e.getEntity() instanceof Creeper && ((Creeper) e.getEntity()).isPowered() && (new Random().nextInt(100) <= rule.getDrop()))
            e.getDrops().add(new ItemStack(Material.TNT, rule.getTntAmount()));
    }

    @EventHandler
    public void dead(PlayerDeathEvent e)
    {
        if(e.getEntity().hasMetadata("NPC") || !Fk.getInstance().getWorldManager().isAffected(e.getEntity().getWorld()))
            return;

        Environment.setDeathMessage(
                e,
                FkPI.getInstance().getTeamManager().getPlayerTeam(e.getEntity()),
                FkPI.getInstance().getTeamManager().getPlayerTeam(e.getEntity().getKiller())
        );

        /*
         * Si le killer a une team
         */
        if(e.getEntity().getKiller() != null && FkPI.getInstance().getTeamManager().getPlayerTeam(e.getEntity().getKiller()) != null)
        {
            /*
             * Sound
             */
            for(Player p : Bukkit.getOnlinePlayers())
                if (Fk.getInstance().getWorldManager().isAffected(p.getWorld()))
                    Fk.getInstance().getDisplayService().playDeathSound(p);

            /*
             * Add kill Si killer != dead
             */
            if(e.getEntity().getKiller() != null && !e.getEntity().getKiller().getName().equals(e.getEntity().getName()))
                Fk.getInstance().getPlayerManager().getPlayer(e.getEntity().getKiller()).addKill();
        }

        /*
         * Si tué ou tueur pas de team pas de deathlimit
         */
        if(Fk.getInstance().getGame().isPreStart() || Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getEntity()) == null || (e.getEntity().getKiller() != null && Fk.getInstance().getFkPI().getTeamManager().getPlayerTeam(e.getEntity().getKiller()) == null))
            return;

        /*
         * Add death
         */
        Fk.getInstance().getPlayerManager().getPlayer(e.getEntity()).addDeath();

        /*
         * DeathLimit > 0
         */
        if(FkPI.getInstance().getRulesManager().getRule(Rule.DEATH_LIMIT) > 0)
            if(Fk.getInstance().getPlayerManager().getPlayer(e.getEntity()).getDeaths() >= FkPI.getInstance().getRulesManager().getRule(Rule.DEATH_LIMIT))
            {
                /*
                 * Elimination
                 */

                Fk.broadcast(Messages.BROADCAST_PLAYER_ELIMINATED.getMessage().replace("%player%", e.getEntity().getDisplayName()));
                e.getEntity().setGameMode(GameMode.SPECTATOR);

                for(Player p : Bukkit.getOnlinePlayers())
                    if (Fk.getInstance().getWorldManager().isAffected(p.getWorld()))
                        Fk.getInstance().getDisplayService().playEliminationSound(p);
            }
            /*
             * Info nbre de vie
             */
            else
                ChatUtils.sendMessage(e.getEntity(), Messages.PLAYER_LIFES_REMAINING.getMessage()
                        .replace("%amount%", String.valueOf(Fk.getInstance().getPlayerManager().getPlayer(e.getEntity()).getDeaths()))
                        .replace("%over%", String.valueOf(FkPI.getInstance().getRulesManager().getRule(Rule.DEATH_LIMIT))
                                .replace("%unit%", Messages.Unit.TRY.tl(Fk.getInstance().getPlayerManager().getPlayer(e.getEntity()).getDeaths())))
                );

        FkPlayer fkP = Fk.getInstance().getPlayerManager().getPlayer(e.getEntity());
        if(fkP.getState() == PlayerState.EDITING_SCOREBOARD)
            fkP.getSbDisplayer().exit();

        Fk.getInstance().getDisplayService().updateAll(PlaceHolder.KILLS_RELATIVE);
    }
}
