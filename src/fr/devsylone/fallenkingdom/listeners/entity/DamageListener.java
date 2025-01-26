package fr.devsylone.fallenkingdom.listeners.entity;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import fr.devsylone.fallenkingdom.players.FkPlayer.PlayerState;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Environment;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.ChargedCreepers;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Base;
import fr.devsylone.fkpi.teams.CrystalCore;
import fr.devsylone.fkpi.teams.Nexus;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (!(entity instanceof EnderCrystal)) {
            return;
        }
        if (!Fk.getInstance().getWorldManager().isAffected(entity.getWorld())) {
            return;
        }
        final Player damager = getOwner(event.getDamager());
        final Team playerTeam = damager == null ? null : FkPI.getInstance().getTeamManager().getPlayerTeam(damager);
        for (Team team : FkPI.getInstance().getTeamManager().getTeams()) {
            final Base base = team.getBase();
            if (base == null) continue;
            final Nexus nexus = base.getNexus();
            if (!(nexus instanceof CrystalCore)) continue;
            // L'entité n'est pas vérifiée comme appartenant à la base, au cas où des joueurs l'auraient déplacée
            final CrystalCore core = (CrystalCore) nexus;
            if (!entity.getUniqueId().equals(core.getEntityId())) continue;
            event.setCancelled(true);
            if (damager == null) return;
            if (!Fk.getInstance().getGame().isAssaultsEnabled() || !FkPI.getInstance().getChestsRoomsManager().isEnabled()) {
                ChatUtils.sendMessage(damager, Messages.PLAYER_TNT_NOT_ACTIVE);
                return;
            }
            if (Fk.getInstance().getGame().isPaused()) {
                ChatUtils.sendMessage(damager, Messages.PLAYER_PAUSE);
                return;
            }
            if (playerTeam == null) {
                ChatUtils.sendMessage(damager, Messages.PLAYER_CHEST_ATTACK_TEAM.getMessage().replace("%team%", team.getName()));
                return;
            }
            if (playerTeam == team) {
                ChatUtils.sendMessage(damager, Messages.PLAYER_CHEST_ATTACK_SELF);
                return;
            }
            if (!core.isInside(damager)) {
                core.addEnemyInside(damager);
            }
            if (Version.VersionType.V1_9_V1_12.isHigherOrEqual()) {
                entity.getWorld().spawnParticle(Particle.CRIT, entity.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
            }
            core.damage(playerTeam, (int) event.getFinalDamage());
            return;
        }
    }

    private static @Nullable Player getOwner(@NotNull Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }
        if (entity instanceof Projectile) {
            final ProjectileSource shooter = ((Projectile) entity).getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
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
