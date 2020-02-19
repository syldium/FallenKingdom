package fr.devsylone.fkpi.rules;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;

import java.util.ArrayList;
import java.util.List;

public class DenyPotions extends Rule
{
    public static final boolean USE_POTION_ADAPTER = Bukkit.getBukkitVersion().contains("1.8");

    private List<String> advancedList = new ArrayList<>();

    public DenyPotions(boolean value)
    {
        super("DenyPotions", value);
    }

    public DenyPotions()
    {
        this(true);
    }

    public boolean isProhibited(ItemStack potion)
    {
        if ((boolean) this.value)
            return true;

        if (USE_POTION_ADAPTER)
            return advancedList.contains(Potion.fromItemStack(potion).getType().getEffectType().getName());
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        return advancedList.contains(potionMeta.getBasePotionData().getType().name());
    }

    public List<String> getAdvancedList()
    {
        return advancedList;
    }

    @Override
    public void load(ConfigurationSection config)
    {
        this.value = config.get("value");
        this.advancedList = config.getStringList("advanced");
    }

    @Override
    public void save(ConfigurationSection config)
    {
        config.set("value", value);
        config.set("advanced", advancedList);
    }
}
