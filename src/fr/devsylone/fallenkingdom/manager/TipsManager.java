package fr.devsylone.fallenkingdom.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.Add;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.players.Tip;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;

public class TipsManager
{
	private final List<Tip> tips = new ArrayList<>();
	private final List<Tip> displayed = new ArrayList<>();
	private final List<Tip> used = new ArrayList<>();
	private final int tipsSize;
	private int task;

	public TipsManager()
	{
		newTip(DoPauseAfterDay.class, "Mettre en pause à la fin de la journée !");
		newTip(TntJump.class, "Empêcher les tours en §cTNT&r !");
		newTip(ChargedCreepers.class, "Gérer les creepers chargés qui donnent de la §cTNT&r !");
		newTip(Edit.class, "Modifier le scoreboard à votre guise ! ");
		newTip(Bug.class, "Un problème ? Alertez l'équipe de développement !");
		newTip(Restore.class, "À utiliser après une pause !");
		newTip(StarterInv.class, "Personnalisez le stuff de départ !");
		newTip(PlaceBlockInCave.class, "Pour poser des blocs en caverne !");
		newTip(Add.class, "Créez des coffres à crocheter !");
		newTip(DayDuration.class, "Changer la durée d'un jour !");
		newTip(DisabledPotions.class, "Désactivez certaines potions de la partie !");

		newTip(null, "Faites §l[tab] &ren écrivant votre commande, elle s'écrira toute seule !");
		newTip(null, "Notre discord : §e§lhttps://discord.gg/NwqFNa6 &r !");
		newTip(null, "Les seaux ne peuvent être posés contre une muraille adverse !");

		tipsSize = tips.size();
	}

	private void newTip(Class<? extends AbstractCommand> cmd, String tip)
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

		Fk.broadcast(" ");
		Fk.broadcast(tip.getChatFormatted(), ChatUtils.TIP, FkSound.NOTE_PLING);
		Fk.broadcast(" ");
		if(displayed.size()+used.size() >= tips.size())
			displayed.clear();
	}

	public void startBroadcasts()
	{
		this.task = new BukkitRunnable() {
			public void run() {
				if(Fk.getInstance().getGame().getState().equals(GameState.BEFORE_STARTING))
					sendRandomTip();
			}
		}.runTaskTimerAsynchronously(Fk.getInstance(), 3 * 60 * 20, 3 * 60 * 20).getTaskId();
	}

	public void addUsed(AbstractCommand cmd)
	{
		Optional<Tip> usedTip = tips.stream().filter(t -> t.getCommandClass() != null && t.getCommandClass().equals(cmd.getClass())).findFirst();
		if(usedTip.isPresent() && !used.contains(usedTip.get()))
			used.add(usedTip.get());
	}

	public void cancelBroadcasts()
	{
		Bukkit.getScheduler().cancelTask(this.task);
	}
}
