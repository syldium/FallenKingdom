package fr.devsylone.fallenkingdom.commands.lang;

import com.google.common.collect.ImmutableList;
import fr.devsylone.fallenkingdom.commands.abstraction.CommandPermission;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.utils.Messages;

public class FkLangCommand extends FkParentCommand
{

    public FkLangCommand() {
        super("lang", ImmutableList.<FkCommand>builder()
                        .add(new SetLang())
                        .add(new TryLoad())
                        .build()
        , Messages.CMD_MAP_LANG, CommandPermission.ADMIN);
    }
}
