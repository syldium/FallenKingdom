package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandRole;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandResult;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.exception.ArgumentParseException;
import fr.devsylone.fallenkingdom.utils.Messages;
import fr.devsylone.fkpi.managers.RulesManager;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class GlobalChatPrefix extends FkCommand {

    public GlobalChatPrefix() {
        super("globalChatPrefix", "[char]", Messages.CMD_MAP_RULES_GLOBAL_CHAT_PREFIX, CommandRole.ADMIN);
    }

    @Override
    public CommandResult execute(Fk plugin, CommandSender sender, List<String> args, String label) {
        RulesManager manager = plugin.getFkPI().getRulesManager();
        if (args.size() < 1) {
            if (manager.getRule(Rule.GLOBAL_CHAT_PREFIX) == ' ') {
                manager.setRule(Rule.GLOBAL_CHAT_PREFIX, Rule.GLOBAL_CHAT_PREFIX.getDefaultValue());
                broadcast(Messages.CMD_RULES_GLOBAL_CHAT_PREFIX_SET.getMessage().replace("%char%", String.valueOf(Rule.GLOBAL_CHAT_PREFIX.getDefaultValue())));
            } else {
                manager.setRule(Rule.GLOBAL_CHAT_PREFIX, ' ');
                broadcast(Messages.CMD_RULES_GLOBAL_CHAT_PREFIX_UNSET.getMessage());
            }
        } else {
            char c = args.get(0).charAt(0);
            if (Character.isWhitespace(c) || Character.isLetterOrDigit(c) || c == '/' || c == ChatColor.COLOR_CHAR) {
                throw new ArgumentParseException(Messages.CMD_ERROR_CHAT_CHAR_FORMAT.getMessage().replace("%char%", String.valueOf(c)));
            }
            manager.setRule(Rule.GLOBAL_CHAT_PREFIX, c);
            broadcast(Messages.CMD_RULES_GLOBAL_CHAT_PREFIX_SET.getMessage().replace("%char%", String.valueOf(c)));
        }
        return CommandResult.SUCCESS;
    }
}
