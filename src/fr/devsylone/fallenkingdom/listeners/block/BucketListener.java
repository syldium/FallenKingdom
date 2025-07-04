package fr.devsylone.fallenkingdom.listeners.block;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.teams.Team;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.jetbrains.annotations.NotNull;

public class BucketListener implements Listener
{
    private final Fk plugin;

    public BucketListener(@NotNull Fk plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void event(PlayerBucketEmptyEvent event) {
        onBucket(event, Messages.PLAYER_PLACE_WATER_NEXT);
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        onBucket(event, Messages.PLAYER_FILL_BUCKET_NEXT);
    }

    @EventHandler
    public void onDispense(@NotNull BlockDispenseEvent event) {
        final Block sourceBlock = event.getBlock();
        if (!plugin.getWorldManager().isWorldWithBase(sourceBlock.getWorld())) {
            return;
        }
        if (!plugin.getFkPI().getRulesManager().getRule(Rule.BLAST_PROOF_BASE)
                && Version.VersionType.V1_13.isHigherOrEqual()
                && event.getItem().getType() == Material.WATER_BUCKET) {
            final BlockData blockData = sourceBlock.getBlockData();
            if (blockData instanceof Directional) {
                final Block targetBlock = sourceBlock.getRelative(((Directional) blockData).getFacing());
                if (plugin.getFkPI().getTeamManager().getBase(targetBlock).isPresent()) {
                    if (targetBlock.getBlockData() instanceof Waterlogged) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private void onBucket(@NotNull PlayerBucketEvent event, Messages message) {
        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();
        if (player.getGameMode() == GameMode.CREATIVE || !plugin.getWorldManager().isWorldWithBase(player.getWorld())) {
            return;
        }

        final Team playerTeam = plugin.getFkPI().getTeamManager().getPlayerTeam(player);
        if (playerTeam == null || plugin.getGame().isPreStart()) {
            return;
        }

        if (!plugin.getFkPI().getRulesManager().getRule(Rule.BUCKET_ASSAULT)) {
            for (Team team : Fk.getInstance().getFkPI().getTeamManager().getTeams()) {
                if (!playerTeam.equals(team)) {
                    if (team.getBase() != null && team.getBase().contains(block, 3)) {
                        ChatUtils.sendMessage(player, message);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        if (!plugin.getFkPI().getRulesManager().getRule(Rule.BLAST_PROOF_BASE) && Version.VersionType.V1_13.isHigherOrEqual()) {
            final BlockData blockData = block.getBlockData();
            if (plugin.getFkPI().getTeamManager().getBase(block).isPresent()) {
                if (blockData instanceof Waterlogged) {
                    ChatUtils.sendMessage(player, message);
                    event.setCancelled(true);
                }
            }
        }
    }
}
