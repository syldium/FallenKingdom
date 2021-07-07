package fr.devsylone.fallenkingdom.commands.teams;

import com.google.common.collect.ImmutableList;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.AbstractCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.commands.teams.teamscommands.*;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;

public class FkTeamCommand extends FkParentCommand
{
    public FkTeamCommand() {
        super("team", ImmutableList.<AbstractCommand>builder()
                .add(new AddPlayer())
                .add(new ChestsRoom())
                .add(new Create())
                .add(new TeamsList())
                .add(new Random())
                .add(new Remove())
                .add(new RemovePlayer())
                .add(new TeamRename())
                .add(new TeamTeleport())
                .add(new SetBase())
                .add(new SetColor())
                .build()
        , Messages.CMD_MAP_TEAM);
    }

    @Override
    protected void broadcast(String message) {
        Fk.broadcast(ChatColor.GOLD + message, ChatUtils.TEAM);
    }
}
