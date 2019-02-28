package fr.devsylone.fallenkingdom.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.Bug;
import fr.devsylone.fallenkingdom.commands.FkCommand;
import fr.devsylone.fallenkingdom.commands.chests.chestscommands.Add;
import fr.devsylone.fallenkingdom.commands.game.gamescommands.Restore;
import fr.devsylone.fallenkingdom.commands.game.gamescommands.StarterInv;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.ChargedCreepers;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.PlaceBlockInCave;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands.DoPauseAfterDay;
import fr.devsylone.fallenkingdom.commands.rules.rulescommands.booleancommands.TntJump;
import fr.devsylone.fallenkingdom.commands.scoreboard.scoreboardcommands.Edit;
import fr.devsylone.fallenkingdom.game.Game.GameState;
import fr.devsylone.fallenkingdom.players.Tip;
import fr.devsylone.fallenkingdom.utils.ChatUtils;
import fr.devsylone.fallenkingdom.utils.FkSound;

public class TipsManager
{
	private HashMap<Integer, Tip> tips;
	private int last = 0;
	private List<Integer> displayed;
	private Set<Integer> used;
	private int task;

	public TipsManager()
	{
		this.tips = new HashMap<Integer, Tip>();
		this.displayed = new ArrayList<Integer>();
		this.used = new HashSet<Integer>();

		newTip(new DoPauseAfterDay(), "Mettre en pause à la fin de la journée !");
		newTip(new TntJump(), "Empêcher les tower en §cTNT&r !");
		newTip(new ChargedCreepers(), "Gérer les creepers chargés qui lootent de la §cTNT&r !");
		newTip(new Edit(), "Modifier le scoreboard à votre guise ! ");
		newTip(new Bug(), "Un problème ? Alertez l'équipe de développement en une commande");
		newTip(new Restore(), "À utiliser après une pause !");
		newTip(new StarterInv(), "Personaliser le stuff de départ");
		newTip(new PlaceBlockInCave(), "Poser des blocs en caverne !");
		newTip(new Add(), "Créer des coffres à crocheter !");

		newTip(null, "Faîtes §l[tab] &ren écrivant votre commande, elle s'ecrira tout seule !");
		newTip(null, "Notre discord : §e§lhttps://discord.gg/NwqFNa6 &r !");
		newTip(null, "Les seaux ne peuvent être posés contre une muraille adverse !");
	}

	private void newTip(FkCommand cmd, String tip)
	{
		tips.put(Integer.valueOf(this.last++), new Tip(cmd, tip));
	}

	public void sendRandomTip()
	{
		Random rdm = new Random();

		int rdmi = -1;
		while(displayed.contains(rdmi) || used.contains(rdmi) || rdmi == -1)
			rdmi = rdm.nextInt(last);

		Tip tip = tips.get(rdmi);
		displayed.add(rdmi);

		Fk.broadcast("");
		Fk.broadcast("");
		Fk.broadcast(tip.getChatFormatted(), ChatUtils.TIP, FkSound.NOTE_PLING);
		Fk.broadcast("");
		Fk.broadcast("");
		if(displayed.size() >= tips.size())
			displayed.clear();
	}

	public void startBroadcasts()
	{
		this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Fk.getInstance(), new Runnable()
		{
			public void run()
			{
				if(Fk.getInstance().getGame().getState().equals(GameState.BEFORE_STARTING))
					sendRandomTip();
			}
		}, 3 * 60 * 20, 3 * 60 * 20);
//				}, 100L, 100L);
	}

	public void addUsed(FkCommand cmd)
	{
		Entry<Integer, Tip> usedTip = null;
		for(Entry<Integer, Tip> tip : tips.entrySet())
			if(tip.getValue().getCommand() != null && tip.getValue().getCommand().getClass().equals(cmd.getClass()))
			{
				usedTip = tip;
				break;
			}
		if(usedTip != null)
			used.add(usedTip.getKey());
	}

	public void cancelBroadcasts()
	{
		Bukkit.getScheduler().cancelTask(this.task);
	}
}
