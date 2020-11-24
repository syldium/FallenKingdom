package fr.devsylone.fallenkingdom.commands.rules;

import com.google.common.collect.ImmutableList;
import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.abstraction.FkCommand;
import fr.devsylone.fallenkingdom.commands.abstraction.FkParentCommand;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.*;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands.*;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.capcommands.*;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.ChatColor;

public class FkRuleCommand extends FkParentCommand
{
	public FkRuleCommand()
	{
		super("rules", ImmutableList.<FkCommand>builder()
				.add(new AllowBlock())
				.add(new CaptureRate())
				.add(new ChargedCreepers())
				.add(new ChestLimit())
				.add(new DayDuration())
				.add(new DeathLimit())
				.add(new DeepPause())
				.add(new DenyBlock())
				.add(new DisabledPotions())
				.add(new DoPauseAfterDay())
				.add(new EndCap())
				.add(new EnderpearlAssault())
				.add(new EternalDay())
				.add(new FriendlyFire())
				.add(new GlobalChatPrefix())
				.add(new HealthBelowName())
				.add(new RulesList())
				.add(new NetherCap())
				.add(new PlaceBlockInCave())
				.add(new PvpCap())
				.add(new RespawnAtHome())
				.add(new TntCap())
				.add(new TntJump())
				.build()
		, Messages.CMD_MAP_RULES);
	}

	@Override
	protected void broadcast(String message) {
		Fk.broadcast(ChatColor.GOLD + message, ChatUtils.RULES);
	}
}
