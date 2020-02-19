package fr.devsylone.fallenkingdom.commands.rules.rulescommands;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.commands.rules.FkRuleCommand;
import fr.devsylone.fallenkingdom.exception.FkLightException;
import fr.devsylone.fallenkingdom.players.FkPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;

public class DenyPotions extends FkRuleCommand
{
    public DenyPotions()
    {
        super("denyPotions", "[true|false] (ou tenir en main une potion)", 0, "Interdit ou non les potions, dans leur globalité ou selon leur type.");
    }

    @Override
    public void execute(Player sender, FkPlayer fkp, String[] args)
    {
        fr.devsylone.fkpi.rules.DenyPotions rule = (fr.devsylone.fkpi.rules.DenyPotions) Fk.getInstance().getFkPI().getRulesManager().getRuleByName("DenyPotions");
        if (args.length > 0 && (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("false")))
        {
            boolean value = Boolean.valueOf(args[0]);
            rule.setValue(value);
            broadcast("Les potions sont désormais", value ? "interdites" : "autorisées", "!");
        }
        else
        {
            ItemStack inHand = sender.getItemInHand();
            if (inHand == null || !inHand.getType().name().contains("POTION"))
                throw new FkLightException(usage);

            PotionMeta meta = (PotionMeta) inHand.getItemMeta();
            String effectType;
            if (rule.USE_POTION_ADAPTER)
                effectType = Potion.fromItemStack(inHand).getType().getEffectType().getName();
            else
                effectType = meta.getBasePotionData().getType().name();

            if (rule.getAdvancedList().contains(effectType))
            {
                rule.getAdvancedList().remove(effectType);
                broadcast("Les potions de", effectType, "sont activées !");
            }
            else if ((boolean) rule.getValue())
            {
                throw new FkLightException("Pour filter les potions par type, veuillez d'abord rétablir les potions.");
            }
            else
            {
                rule.getAdvancedList().add(effectType);
                broadcast("Les potions de", effectType, "sont désactivées !");
            }
        }
    }
}
