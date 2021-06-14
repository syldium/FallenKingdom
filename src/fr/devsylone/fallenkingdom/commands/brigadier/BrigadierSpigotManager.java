package fr.devsylone.fallenkingdom.commands.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import fr.devsylone.fallenkingdom.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import java.util.Map;

/**
 * Implémentation Spigot pour Brigadier.
 * @param <S>
 */
public class BrigadierSpigotManager<S> extends BrigadierManager<S> implements Listener {

    private final List<LiteralCommandNode<S>> registeredNodes = new ArrayList<>();

    private static final Field CONSOLE_FIELD;
    private static final Method GET_COMMAND_DISPATCHER_METHOD;
    private static final Method GET_BRIGADIER_DISPATCHER_METHOD;
    private static final Method GET_BUKKIT_SENDER_METHOD;
    private static final Constructor<?> COMMAND_WRAPPER_CONSTRUCTOR;
    private static final Field[] CHILDREN_FIELDS;

    static {
        try {
            Class<?> craftServer = NMSUtils.obcClass("CraftServer");
            CONSOLE_FIELD = craftServer.getDeclaredField("console");
            CONSOLE_FIELD.setAccessible(true);

            Class<?> minecraftServer = NMSUtils.nmsClass("server", "MinecraftServer");
            GET_COMMAND_DISPATCHER_METHOD = minecraftServer.getDeclaredMethod("getCommandDispatcher");
            GET_COMMAND_DISPATCHER_METHOD.setAccessible(true);

            Class<?> commandDispatcher = NMSUtils.nmsClass("commands", "CommandDispatcher");
            GET_BRIGADIER_DISPATCHER_METHOD = Arrays.stream(commandDispatcher.getDeclaredMethods())
                    .filter(method -> method.getParameterCount() == 0)
                    .filter(method -> CommandDispatcher.class.isAssignableFrom(method.getReturnType()))
                    .findFirst().orElseThrow(NoSuchMethodException::new);
            GET_BRIGADIER_DISPATCHER_METHOD.setAccessible(true);

            Class<?> commandListenerWrapper = NMSUtils.nmsClass("commands", "CommandListenerWrapper");
            GET_BUKKIT_SENDER_METHOD = commandListenerWrapper.getDeclaredMethod("getBukkitSender");
            GET_BUKKIT_SENDER_METHOD.setAccessible(true);

            Class<?> bukkitCommandWrapper = NMSUtils.obcClass("command.BukkitCommandWrapper");
            COMMAND_WRAPPER_CONSTRUCTOR = bukkitCommandWrapper.getConstructor(craftServer, Command.class);

            Field childrenField = CommandNode.class.getDeclaredField("children");
            Field literalsField = CommandNode.class.getDeclaredField("literals");
            Field argumentsField = CommandNode.class.getDeclaredField("arguments");
            CHILDREN_FIELDS = new Field[] {childrenField, literalsField, argumentsField};
            for (Field field : CHILDREN_FIELDS) {
                field.setAccessible(true);
            }
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public BrigadierSpigotManager(Plugin plugin) {
        super(BrigadierSpigotManager::getBukkitSender);
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

    private void removeChild(RootCommandNode<S> root, String name) {
        try {
            for (Field field : CHILDREN_FIELDS) {
                // noinspection unchecked
                Map<String, CommandNode<S>> children = (Map<String, CommandNode<S>>) field.get(root);
                children.remove(name);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static CommandSender getBukkitSender(Object commandWrapperListener) {
        try {
            return (CommandSender) GET_BUKKIT_SENDER_METHOD.invoke(commandWrapperListener);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        CommandDispatcher<S> dispatcher = getDispatcher();
        for (LiteralCommandNode<S> node : registeredNodes) {
            removeChild(dispatcher.getRoot(), node.getName());
            dispatcher.getRoot().addChild(node);
        }
    }
}