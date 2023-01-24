package fr.devsylone.fallenkingdom.version.packet.book;

import fr.devsylone.fallenkingdom.version.component.FkBook;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BookViewer {

    @Nullable BookViewer INSTANCE = Provider.VIEWER;

    void openBook(@NotNull Player player, @NotNull FkBook book);
}
