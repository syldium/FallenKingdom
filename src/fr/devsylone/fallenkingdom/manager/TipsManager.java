package fr.devsylone.fallenkingdom.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import fr.devsylone.fallenkingdom.commands.abstraction.AbstractCommand;
import fr.devsylone.fallenkingdom.commands.debug.Bug;
import fr.devsylone.fallenkingdom.commands.game.gamescommands.Restore;
import fr.devsylone.fallenkingdom.commands.game.gamescommands.StarterInv;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.ChargedCreepers;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.DayDuration;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.DisabledPotions;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.PlaceBlockInCave;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands.DoPauseAfterDay;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands.TntJump;
import fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands.Edit;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.Add;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.ChestLock;
import fr.devsylone.fallenkingdom.players.Tip;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;

public class TipsManager
{
	private final List<Tip> tips = new ArrayList<>();
	private final Set<Tip> displayed = new HashSet<>();
	private final Set<Tip> used = new HashSet<>();
	private final int tipsSize;
	private int task;

	public TipsManager()
	{
		newTip(DoPauseAfterDay.class, Messages.TIP_DO_PAUSE_AFTER_DAY);
		newTip(TntJump.class, Messages.TIP_TNT_JUMP);
		newTip(ChargedCreepers.class, Messages.TIP_CHARGED_CREEPERS);
		newTip(Edit.class, Messages.TIP_SCOREBOARD_EDIT);
		newTip(Bug.class, Messages.TIP_BUG);
		newTip(Restore.class, Messages.TIP_RESTORE);
		newTip(StarterInv.class, Messages.TIP_STARTER_INV);
		newTip(PlaceBlockInCave.class, Messages.TIP_PLACE_BLOCK_IN_CAVE);
		newTip(Add.class, Messages.TIP_LOCKED_CHEST);
		newTip(DayDuration.class, Messages.TIP_DAY_DURATION);
		newTip(DisabledPotions.class, Messages.TIP_DISABLED_POTIONS);
		newTip(ChestLock.class, Messages.TIP_CHEST_LOCK);

		newTip(null, Messages.TIP_TAB_COMPLETION);
		newTip(null, Messages.TIP_DISCORD);
		newTip(null, Messages.TIP_WATER_NEXT_TO_BASE);
		newTip(null, Messages.TIP_FK_ADVERTISEMENTS);

		tipsSize = tips.size();
	}

	private void newTip(Class<? extends AbstractCommand> cmd, Messages tip)
	{
		tips.add(new Tip(cmd, tip));
	}

	public void sendRandomTip()
	{
		Random rdm = new Random();

		Tip tip = null;
		while(tip == null || displayed.contains(tip) || used.contains(tip))
			tip = tips.get(rdm.nextInt(tipsSize));

		displayed.add(tip);

		if(!tip.getTip().isEmpty())
		{
			Fk.broadcast(" ");
			Fk.broadcast(tip.getChatFormatted(), ChatUtils.TIP, FkSound.NOTE_PLING);
			Fk.broadcast(" ");
		}
		if(displayed.size()+used.size() >= tips.size())
			displayed.clear();
	}

	public void startBroadcasts()
	{
		this.task = new BukkitRunnable() {
			@Override
			public void run() {
				if(Fk.getInstance().getGame().isPreStart())
					sendRandomTip();
			}
		}.runTaskTimerAsynchronously(Fk.getInstance(), 3 * 60 * 20, 3 * 60 * 20).getTaskId();
	}

	public void addUsed(AbstractCommand cmd)
	{
		Optional<Tip> usedTip = tips.stream().filter(t -> t.getCommandClass() != null && t.getCommandClass().equals(cmd.getClass())).findFirst();
		if(usedTip.isPresent())
			used.add(usedTip.get());
	}

	public void cancelBroadcasts()
	{
		Bukkit.getScheduler().cancelTask(this.task);
	}
}
