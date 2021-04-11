package fr.devsylone.fallenkingdom.version;

import com.cryptomorin.xseries.XMaterial;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fallenkingdom.utils.XBlock;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static fr.devsylone.fallenkingdom.version.Environment.getMinHeight;

/**
 * Cherche un endroit sûr pour téléporter.
 */
public class SafeLocationSearcher {

    private static final Set<Material> DAMAGING_TYPES = XBlock.materialSet(
            XMaterial.CACTUS, XMaterial.CAMPFIRE, XMaterial.FIRE, XMaterial.MAGMA_BLOCK, XMaterial.SOUL_CAMPFIRE, XMaterial.SOUL_FIRE, XMaterial.SWEET_BERRY_BUSH, XMaterial.WITHER_ROSE
    );

    private final Location around;

    /**
     * Initialise la recherche d'une {@link Location}.
     *
     * @param around À chercher autour
     */
    public SafeLocationSearcher(Location around) {
        Objects.requireNonNull(around, "Destination not set.");
        Objects.requireNonNull(around.getWorld(), "Destination world not set.");
        this.around = around;
    }

    /**
     * Effectue la recherche d'une {@link Location} sûre.
     *
     * <p>Noter que le chunk dans lequel la téléportation pourrait
     * se faire a déjà été chargé lors des vérifications des blocs.</p>
     *
     * @param radius Éloignement maximal des recherches
     * @return Une {@link Location} bientôt résolue
     * @throws LocationNotFound Si aucun endroit ne convient
     */
    public CompletableFuture<Location> find(int radius) {
        // Liste des chunks à possiblement vérifier
        Map<Block2DPos, List<Block2DPos>> chunks = new HashMap<>();
        for (int dx = -radius; dx < radius; dx++) {
            for (int dz = -radius; dz < radius; dz++) {
                int x = around.getBlockX() + dx;
                int z = around.getBlockZ() + dz;

                Block2DPos chunk = new Block2DPos(x >> 4, z >> 4);
                Block2DPos pos = new Block2DPos(x & 0xF, z & 0xF);
                chunks.computeIfAbsent(chunk, s -> new ArrayList<>()).add(pos);
            }
        }

        // Plus qu'à chercher par chunk
        CompletableFuture<Location> future = new CompletableFuture<>();
        for (Block2DPos chunkPos : chunks.keySet()) {
            check(around, chunkPos, chunks.get(chunkPos), future, radius);
        }
        future.completeExceptionally(new LocationNotFound());
        return future;
    }

    private void check(Location base, Block2DPos chunkPos, List<Block2DPos> positions, CompletableFuture<Location> future, int radius) {
        if (future.isCancelled() || future.isDone() || future.isCompletedExceptionally()) {
            return;
        }

        World world = around.getWorld();
        Environment.getChunkAtAsync(world, chunkPos.x, chunkPos.z).thenApply(chunk -> {
            for (Block2DPos pos : positions) {
                int upY = Math.min(base.getBlockY() + 3 + radius, world.getMaxHeight() - 1);
                int downY = Math.max(base.getBlockY() - 2 - radius, getMinHeight(world));
                Location loc = getSafeDestination(chunk, pos.x, pos.z, upY, downY);
                if (loc != null) {
                    future.complete(loc);
                    return true;
                }
            }
            return false;
        }).exceptionally(future::completeExceptionally);
    }

    private Location getSafeDestination(Chunk chunk, int x, int z, int upY, int downY) {
        int y = upY;
        Block block = chunk.getBlock(x, y, z);
        Material type;
        int airCount = 0;
        while (y > downY) {
            airCount = XBlock.isReplaceable(block) && !block.isLiquid() ? airCount + 1 : 0;
            y -= 1;
            block = chunk.getBlock(x, y, z);
            type = block.getType();
            if (airCount > 1 && !XBlock.isReplaceable(block)) {
                if (!DAMAGING_TYPES.contains(type) && !block.isLiquid()) {
                    return block.getLocation().add(0.5, 1, 0.5);
                }
            }
        }
        return null;
    }

    static class Block2DPos {
        final int x;
        final int z;

        Block2DPos(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Block2DPos)) return false;
            Block2DPos that = (Block2DPos) other;
            return x == that.x && z == that.z;
        }

        @Override
        public int hashCode() {
            return x & 0xffff | (z & 0xffff) << 16;
        }
    }

    public static class LocationNotFound extends RuntimeException {
        LocationNotFound() {
            super(Messages.PLAYER_BASE_OBSTRUCTED.getMessage());
        }
    }
}
