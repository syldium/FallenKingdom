package fr.devsylone.fallenkingdom.version.packet.book;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fallenkingdom.version.component.FkBook;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

class BookViewer1_8 implements BookViewer {

    private static final Constructor<?> PACKET_CUSTOM_PAYLOAD;
    private static final Constructor<?> PACKET_DATA_SERIALIZER;

    static {
        try {
            final String packetsPackage = "network.protocol.game";
            final Class<?> packetCustomPayloadClass = NMSUtils.nmsClass(packetsPackage, "PacketPlayOutCustomPayload");
            final Class<?> packetSerializerClass = NMSUtils.nmsClass("PacketDataSerializer");
            PACKET_CUSTOM_PAYLOAD = packetCustomPayloadClass.getConstructor(String.class, packetSerializerClass);
            PACKET_DATA_SERIALIZER = packetSerializerClass.getConstructor(ByteBuf.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public void openBook(@NotNull Player p, @NotNull FkBook book) {
        final int slot = p.getInventory().getHeldItemSlot();
        final ItemStack original = p.getInventory().getItem(slot);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "replaceitem entity " + p.getName() + " slot.hotbar." + slot + " minecraft:written_book 1 0 " + book.nbt());

        Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
            try {
                if (original != null && original.getType() != Material.AIR) {
                    p.getWorld().dropItem(p.getLocation(), original).setPickupDelay(0);
                }

                p.getInventory().setHeldItemSlot(slot);

                ByteBuf buf = Unpooled.buffer(256);
                buf.setByte(0, (byte) 0);
                buf.writerIndex(1);
                Object payload = PACKET_CUSTOM_PAYLOAD.newInstance("MC|BOpen", PACKET_DATA_SERIALIZER.newInstance(buf));

                PacketUtils.sendPacket(p, payload);
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }, 5L);
    }
}
