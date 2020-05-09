package fr.devsylone.fallenkingdom.commands.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implémentation Spigot pour Brigadier.
 * @param <S>
 */
public class BrigadierSpigotManager<S> extends BrigadierManager<S> implements Listener {

    private final List<LiteralCommandNode<S>> registeredNodes = new ArrayList<>();

    private static final Field CONSOLE_FIELD;
    private static final Method GET_COMMAND_DISPATCHER_METHOD;
    private static final Method GET_BRIGADIER_DISPATCHER_METHOD;
    private static final Constructor<?> COMMAND_WRAPPER_CONSTRUCTOR;

    static {
        try {
            Class<?> craftServer = NMSUtils.obcClass("CraftServer");
            CONSOLE_FIELD = craftServer.getDeclaredField("console");
            CONSOLE_FIELD.setAccessible(true);

            Class<?> minecraftServer = NMSUtils.nmsClass("MinecraftServer");
            GET_COMMAND_DISPATCHER_METHOD = minecraftServer.getDeclaredMethod("getCommandDispatcher");
            GET_COMMAND_DISPATCHER_METHOD.setAccessible(true);

            Class<?> commandDispatcher = NMSUtils.nmsClass("CommandDispatcher");
            GET_BRIGADIER_DISPATCHER_METHOD = Arrays.stream(commandDispatcher.getDeclaredMethods())
                    .filter(method -> method.getParameterCount() == 0)
                    .filter(method -> CommandDispatcher.class.isAssignableFrom(method.getReturnType()))
                    .findFirst().orElseThrow(NoSuchMethodException::new);
            GET_BRIGADIER_DISPATCHER_METHOD.setAccessible(true);

            Class<?> bukkitCommandWrapper = NMSUtils.obcClass("command.BukkitCommandWrapper");
            COMMAND_WRAPPER_CONSTRUCTOR = bukkitCommandWrapper.getConstructor(craftServer, Command.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public BrigadierSpigotManager(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void register(CommandManager commandManager, PluginCommand pluginCommand) {
        LiteralCommandNode<S> node = register(commandManager, pluginCommand.getLabel(), getSuggestionProvider(pluginCommand));
        registeredNodes.add(node);
    }

    private CommandDispatcher<S> getDispatcher() {
        try {
            Object mcServerObject = CONSOLE_FIELD.get(Bukkit.getServer());
            Object commandDispatcherObject = GET_COMMAND_DISPATCHER_METHOD.invoke(mcServerObject);
            // noinspection unchecked
            return (CommandDispatcher<S>) GET_BRIGADIER_DISPATCHER_METHOD.invoke(commandDispatcherObject);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private SuggestionProvider<S> getSuggestionProvider(PluginCommand command) {
        try {
            // noinspection unchecked
            return (SuggestionProvider<S>) COMMAND_WRAPPER_CONSTRUCTOR.newInstance(Bukkit.getServer(), command);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        CommandDispatcher<S> dispatcher = getDispatcher();
        for (LiteralCommandNode<S> node : registeredNodes) {
            dispatcher.getRoot().addChild(node);
        }
    }
}