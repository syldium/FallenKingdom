package fr.devsylone.fallenkingdom.version.packet.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static fr.devsylone.fallenkingdom.version.Environment.hasMultiBlockChange;

public interface MultiBlockChange {

    void change(@NotNull Block block, @NotNull Material material);

    void send(@NotNull Player player);

    void cancel(@NotNull Player player);

    @Contract("-> new")
    static @NotNull MultiBlockChange create() {
        if (hasMultiBlockChange()) {
            return new PaperMultiBlockChange();
        }
        return new NMSMultiBlockChange();
    }
}
