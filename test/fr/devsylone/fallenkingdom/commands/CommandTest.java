package fr.devsylone.fallenkingdom.commands;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.manager.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class CommandTest {

    public CommandManager commandManager = MockUtils.getPluginMockSafe().getCommandManager();

    public void assertRun(CommandSender sender, String literal, CommandResult excepted) {
        CommandResult result = commandManager.executeCommand(MockUtils.getPluginMockSafe(), sender, literal);
        String message = CommandResult.SUCCESS.equals(excepted) ? "Command `" + literal + "` should be executed successfully." : "Command `" + literal + "` should fail at runtime.";
        assertEquals(excepted, result, message);
    }

    public void assertRun(CommandSender sender, String literal) {
        assertRun(sender, literal, CommandResult.SUCCESS);
    }

    public void assertRun(String literal) {
        assertRun(Bukkit.getConsoleSender(), literal, CommandResult.SUCCESS);
    }
    public void assertRun(String literal, CommandResult excepted) {
        assertRun(Bukkit.getConsoleSender(), literal, excepted);
    }

}
