package fr.devsylone.fallenkingdom.commands;

import fr.devsylone.fallenkingdom.commands.abstraction.AbstractCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Permissions {

    public static void main(String[] args) throws IOException {
        final Map<String, Boolean> admin = new HashMap<>();
        final Map<String, Boolean> player = new HashMap<>();
        buildPermissionTree(new CommandManager(true).getMainCommands(), admin, player);

        final Path pluginDescriptionPath = FileSystems.getDefault().getPath("resources/plugin.yml");

        final FileConfiguration config;
        if (Files.exists(pluginDescriptionPath)) {
            config = YamlConfiguration.loadConfiguration(pluginDescriptionPath.toFile());
        } else {
            config = new YamlConfiguration();
        }
        config.getRoot().options().pathSeparator(',');

        final ConfigurationSection section = config.createSection("permissions");
        buildPermissionConfig(section.createSection("fallenkingdom.admin"), PermissionDefault.OP, admin);
        buildPermissionConfig(section.createSection("fallenkingdom.player"), PermissionDefault.TRUE, player);

        System.out.println(config.saveToString());
    }

    static void buildPermissionTree(List<? extends AbstractCommand> commands, Map<String, Boolean> admin, Map<String, Boolean> player) {
        Queue<AbstractCommand> queue = new ArrayDeque<>(commands);
        while (!queue.isEmpty()) {
            AbstractCommand command = queue.poll();
            if (command instanceof FkParentCommand) {
                queue.addAll(((FkParentCommand) command).getChildren());
            }
            if (command.getRole() == CommandRole.ADMIN) {
                admin.put(command.getPermission(), true);
            } else {
                player.put(command.getPermission(), true);
            }
        }
    }

    static void buildPermissionConfig(@NotNull ConfigurationSection section, @NotNull PermissionDefault permissionDefault, @NotNull Map<String, Boolean> children) {
        section.set("default", permissionDefault.toString());
        section.set("children", children);
    }
}
