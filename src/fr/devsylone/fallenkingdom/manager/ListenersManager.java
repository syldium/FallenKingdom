package fr.devsylone.fallenkingdom.manager;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.listeners.RuleChangeListener;
import fr.devsylone.fallenkingdom.listeners.TeamChangeListener;
import fr.devsylone.fallenkingdom.listeners.block.BlockExplodeListener;
import fr.devsylone.fallenkingdom.listeners.block.BlockListener;
import fr.devsylone.fallenkingdom.listeners.block.BucketListener;
import fr.devsylone.fallenkingdom.listeners.block.LockedChestInteractListener;
import fr.devsylone.fallenkingdom.listeners.block.PortalCreateListener;
import fr.devsylone.fallenkingdom.listeners.entity.DamageListener;
import fr.devsylone.fallenkingdom.listeners.entity.mob.MobSpawn;
import fr.devsylone.fallenkingdom.listeners.entity.player.AdvancementListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.ChangeGamemodeListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.ChatPreviewListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.DisabledPotionsListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.FoodListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.GoToNetherListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.InventoryListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.JoinListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.MoveListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.PauseInteractionListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.PvpListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.RespawnListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.ScrollListener;
import fr.devsylone.fallenkingdom.listeners.entity.player.UsePortalListener;
import fr.devsylone.fallenkingdom.version.Version;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;

public class ListenersManager
{
	public static void registerListeners(Fk plugin)
	{
		final PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvents(new BlockListener(plugin), plugin);
		pm.registerEvents(new BucketListener(plugin), plugin);

        // Chat-dependent listener
		pm.registerEvents(Fk.getInstance().getChatKind().getListener(), plugin);

		pm.registerEvents(new JoinListener(), plugin);
		pm.registerEvents(new MoveListener(), plugin);
		pm.registerEvents(new PvpListener(), plugin);
		pm.registerEvents(new DamageListener(), plugin);
		pm.registerEvents(new GoToNetherListener(), plugin);
		pm.registerEvents(new FoodListener(), plugin);
		pm.registerEvents(new MobSpawn(), plugin);
		pm.registerEvents(new UsePortalListener(), plugin);
		pm.registerEvents(new InventoryListener(), plugin);
		pm.registerEvents(new ScrollListener(), plugin);
		pm.registerEvents(new LockedChestInteractListener(), plugin);
		pm.registerEvents(new BlockExplodeListener(), plugin);
		pm.registerEvents(new PortalCreateListener(), plugin);
		pm.registerEvents(new ChangeGamemodeListener(), plugin);
		pm.registerEvents(new RespawnListener(), plugin);
		pm.registerEvents(new PauseInteractionListener(), plugin);
		pm.registerEvents(new DisabledPotionsListener(), plugin);
		pm.registerEvents(new RuleChangeListener(plugin), plugin);
		pm.registerEvents(new TeamChangeListener(plugin), plugin);
		if (Version.classExists("org.bukkit.event.player.PlayerAdvancementDoneEvent"))
			pm.registerEvents(new AdvancementListener(), plugin);

		try {
			if (AsyncPlayerChatEvent.class.isAssignableFrom(Class.forName("org.bukkit.event.player.AsyncPlayerChatPreviewEvent"))) {
				pm.registerEvents(new ChatPreviewListener(), plugin);
			}
		} catch (ClassNotFoundException ignored) {}
	}
}
