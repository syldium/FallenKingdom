package fr.devsylone.fallenkingdom.manager.saveable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.version.Version;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import fr.devsylone.fallenkingdom.utils.NMSUtils;
import fr.devsylone.fallenkingdom.utils.PacketUtils;
import fr.devsylone.fkpi.util.Saveable;
import org.bukkit.potion.PotionEffectType;

public class DeepPauseManager implements Saveable
{
    private final List<LivingEntity> noAI = new ArrayList<>();
    private final List<Entity> unDespawnable = new ArrayList<>();

    private static final boolean VERSION1_8 = Bukkit.getBukkitVersion().contains("1.8");
    private static Method NMS_ENTITY_GETNBTTAG;
    private static Method NMS_ENTITY_C;
    private static Constructor<?> NMS_NBTTAG;
    private static Method NMS_NBTTAG_INT;
    private static Method NMS_ENTITY_F;

    private static Field FIELD_ITEM;
    private static Field FIELD_AGE;

    protected static void init()
    {
        try {
            if (VERSION1_8) {
                Class<?> entity = NMSUtils.nmsClass("world.entity", "Entity");
                NMS_ENTITY_GETNBTTAG = entity.getDeclaredMethod("getNBTTag");
                Class<?> nbtTagCompound = NMSUtils.nmsClass("nbt", "NBTTagCompound");
                NMS_NBTTAG = nbtTagCompound.getConstructor();
                NMS_ENTITY_C = entity.getDeclaredMethod("c", nbtTagCompound);
                NMS_NBTTAG_INT = nbtTagCompound.getDeclaredMethod("setInt", String.class, int.class);
                NMS_ENTITY_F = entity.getDeclaredMethod("f", nbtTagCompound);
            }
            Class<?> craftItem = NMSUtils.obcClass("entity.CraftItem");
            try {
                FIELD_ITEM = craftItem.getDeclaredField("item");
            } catch (NoSuchFieldException e) {
                FIELD_ITEM = craftItem.getSuperclass().getDeclaredField("entity");
            }
            FIELD_ITEM.setAccessible(true);

            Class<?> nmsItem = FIELD_ITEM.getType();
            FIELD_AGE = NMSUtils.getField(nmsItem, Integer.TYPE, field -> !Modifier.isStatic(field.getModifiers()));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public DeepPauseManager()
    {
        init();
    }

    public void unfreezePlayers()
    {
        // Dans le cas d'une pause faite en 2.17.1 ou 2.17.2
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getWalkSpeed() < 0.02f && player.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
                player.setWalkSpeed(0.2f);
                player.removePotionEffect(PotionEffectType.WATER_BREATHING);
            }
        });
    }

    public void protectDespawnItems()
    {
        if(FkPI.getInstance().getRulesManager().getRule(Rule.DEEP_PAUSE))
        {
            for(World world : Bukkit.getWorlds())
            {
                if (!Fk.getInstance().getWorldManager().isAffected(world)) {
                    continue;
                }
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof Item) {
                        setItemAge((Item) entity, Short.MIN_VALUE);
                        unDespawnable.add(entity);
                    }
                }
            }
        }
    }

    public void unprotectItems()
    {
        unDespawnable.forEach(item -> setItemAge((Item) item, 0));
        unDespawnable.clear();
    }

    public void removeAIs()
    {
        if(FkPI.getInstance().getRulesManager().getRule(Rule.DEEP_PAUSE))
        {
            for(World world : Bukkit.getWorlds())
            {
                if (!Fk.getInstance().getWorldManager().isAffected(world)) {
                    continue;
                }
                world.getLivingEntities().stream()
                    .filter(entity -> !(entity instanceof Player))
                    .forEach(entity -> {
                        setAI(entity, false);
                        noAI.add(entity);
                    });
            }
        }
    }

    public void resetAIs()
    {
        noAI.forEach(entity -> setAI(entity, true));
        noAI.clear();
    }

    @Override
    public void load(ConfigurationSection config)
    {
        if(config.contains("noAI")) {
            List<String> uuids = config.getStringList("noAI");
            noAI.addAll(
                uuids.stream().map(id -> (LivingEntity) getEntity(UUID.fromString(id)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())
            );
        }

        if(config.contains("UnDespawnable")) {
            List<String> uuids = config.getStringList("UnDespawnable");
            unDespawnable.addAll(
                uuids.stream().map(id -> getEntity(UUID.fromString(id)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())
            );
        }
    }

    @Override
    public void save(ConfigurationSection config)
    {
        List<String> noAiIds = noAI.stream().map(DeepPauseManager::apply).collect(Collectors.toList());
        List<String> itemIds = unDespawnable.stream().map(DeepPauseManager::apply).collect(Collectors.toList());

        config.set("noAI", noAiIds);
        config.set("UnDespawnable", itemIds);
    }

    private Entity getEntity(UUID uuid)
    {
        if (Version.VersionType.V1_13.isHigherOrEqual())
            return Bukkit.getEntity(uuid);
        for (World world : Bukkit.getWorlds())
        {
            for (Entity e : world.getEntities())
            {
                if (e.getUniqueId().equals(uuid)) {
                    return e;
                }
            }
        }
        return null;
    }

    private void setAI(LivingEntity entity, boolean active)
    {
        if (!VERSION1_8) {
            entity.setAI(active);
            return;
        }
        try {
            Object nmsEntity = PacketUtils.getNMSEntity(entity);
            Object tag = NMS_ENTITY_GETNBTTAG.invoke(nmsEntity);

            if(tag == null)
                tag = NMS_NBTTAG.newInstance();
            NMS_ENTITY_C.invoke(nmsEntity, tag);
            NMS_NBTTAG_INT.invoke(tag, "NoAI", active ? 0 : 1);
            NMS_ENTITY_F.invoke(nmsEntity, tag);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private void setItemAge(Item item, int age)
    {
        try {
            Object entityItem = FIELD_ITEM.get(item);
            FIELD_AGE.set(entityItem, age);
        } catch(ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private static String apply(Entity e)
    {
        return e.getUniqueId().toString();
    }
}
