package fr.devsylone.fallenkingdom.commands;

import fr.devsylone.fallenkingdom.commands.abstraction.AbstractCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Permissions {

    public static void main(String[] args) throws IOException, InvalidConfigurationException {
        final Map<String, Boolean> admin = new LinkedHashMap<>();
        final Map<String, Boolean> player = new LinkedHashMap<>();
        buildPermissionTree(new CommandManager(true).getMainCommands(), admin, player);

        final Path pluginDescriptionPath = FileSystems.getDefault().getPath("resources/plugin.yml");

        final FileConfiguration config = new YamlConfiguration();
        config.options().pathSeparator(',');
        if (Files.exists(pluginDescriptionPath)) {
            config.load(pluginDescriptionPath.toFile());
        }

        ConfigurationSection section = config.getConfigurationSection("permissions");
        if (section == null) {
            section = config.createSection("permissions");
        }
        buildPermissionConfig(section, "fallenkingdom.admin", PermissionDefault.OP, admin);
        buildPermissionConfig(section,"fallenkingdom.player", PermissionDefault.TRUE, player);

        System.out.println(config.saveToString());
    }

    static void buildPermissionTree(List<? extends AbstractCommand> commands, Map<String, Boolean> admin, Map<String, Boolean> player) {
        final Queue<AbstractCommand> queue = new ArrayDeque<>(commands);
        while (!queue.isEmpty()) {
            final AbstractCommand command = queue.poll();
            if (command instanceof FkParentCommand) {
                queue.addAll(((FkParentCommand) command).getChildren());
            }
            for (Map.Entry<String, CommandRole> entry : command.getPermissions().entrySet()) {
                if (entry.getValue() == CommandRole.ADMIN) {
                    admin.put(entry.getKey(), true);
                } else {
                    player.put(entry.getKey(), true);
                }
            }
        }
    }

    static void buildPermissionConfig(@NotNull ConfigurationSection parentSection, @NotNull String path, @NotNull PermissionDefault permissionDefault, @NotNull Map<String, Boolean> children) {
        ConfigurationSection section = parentSection.getConfigurationSection(path);
        if (section == null) {
            section = parentSection.createSection(path);
        }
        section.set("default", permissionDefault.toString());
        section.set("children", children);
    }
}
