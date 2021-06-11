package fr.devsylone.fallenkingdom.manager.packets;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static fr.devsylone.fallenkingdom.utils.PacketUtils.MINECRAFT_CHUNK;

public class PacketManager1_17 extends PacketManager {

    private static final Constructor<?> PACKET_CHUNK;

    static {
        try {
            PACKET_CHUNK = Arrays.stream(NMSUtils.nmsClass("network.protocol.game", "PacketPlayOutMapChunk").getConstructors())
                    .filter(constructor -> constructor.getParameterCount() > 0 && constructor.getParameterTypes()[0].isAssignableFrom(MINECRAFT_CHUNK))
                    .findAny().orElseThrow(RuntimeException::new);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    protected int sendSpawn(Player p, Location loc) {
        // TODO
        return 0;
    }

    @Override
    protected void sendMetadata(int id, boolean visible, String customName) {
        // TODO
    }

    @Override
    protected void sendTeleport(int id, Location newLoc) {
        // TODO
    }

    @Override
    protected void sendDestroy(int id) {
        // TODO
    }

    @Override
    protected void sendEquipment(int id, ItemSlot slot, Material material) {
        // TODO
    }

    @Override
    public void sendBlockChange(Player player, Location loc, Material newBlock) {
        player.sendBlockChange(loc, newBlock.createBlockData());
    }

    @Override
    public void sendChunkReset(Player player, Chunk chunk) {
        try {
            PacketUtils.sendPacket(player, PACKET_CHUNK.newInstance(PacketUtils.getNMSChunk(chunk)));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(title, subtitle, 20, 20, 20);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    protected void sendTitlePacket(Player player, TitleType type, String text, int fadeIn, int stay, int fadeOut) {
        throw new UnsupportedOperationException("PacketManager#sendTitle should not use NMS with modern versions.");
    }

    @Override
    public void openBook(Player p, String nbtTags) {
        // TODO
    }
}
