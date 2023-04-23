package fr.devsylone.fallenkingdom.version.packet.book;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fallenkingdom.version.Environment;
import fr.devsylone.fallenkingdom.version.component.FkBook;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class BookViewer1_9 implements BookViewer {

    @Override
    public void openBook(@NotNull Player p, @NotNull FkBook book) {
        final int slot = p.getInventory().getHeldItemSlot();
        ItemStack original = p.getInventory().getItem(slot);

        if (Environment.hasSpigotBookPages()) {
            p.getInventory().setItemInMainHand(book.asItemStack());
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "replaceitem entity " + p.getName() + " slot.hotbar." + slot + " minecraft:written_book 1 0 " + book.nbt());
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Fk.getInstance(), () -> {
            try {
                if (original != null && original.getType() != Material.AIR) {
                    p.getWorld().dropItem(p.getLocation(), original).setPickupDelay(0);
                }

                p.getInventory().setHeldItemSlot(slot);

                ByteBuf buf = Unpooled.buffer(256);
                buf.setByte(0, (byte) 0);
                buf.writerIndex(1);
                Object payload = NMSUtils.getClass("PacketPlayOutCustomPayload").getDeclaredConstructor(String.class, NMSUtils.getClass("PacketDataSerializer")).newInstance("MC|BOpen", NMSUtils.getClass("PacketDataSerializer").getDeclaredConstructor(ByteBuf.class).newInstance(buf));

                PacketUtils.sendPacket(p, payload);
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }, 5L);
    }
}
