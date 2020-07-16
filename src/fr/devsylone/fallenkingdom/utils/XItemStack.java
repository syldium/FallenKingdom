package fr.devsylone.fallenkingdom.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XItemStack {

    private final static Class<?> CHAT_COMPONENT;
    private final static Method CHAT_COMPONENT_FROM_JSON;
    private final static Method CHAT_COMPONENT_TO_JSON;

    private final static boolean HAS_COMPONENT_API;

    static {
        try {
            CHAT_COMPONENT = NMSUtils.nmsClass("IChatBaseComponent");
            Class<?> chatSerializer = NMSUtils.nmsClass("IChatBaseComponent$ChatSerializer");

            CHAT_COMPONENT_FROM_JSON = Arrays.stream(chatSerializer.getDeclaredMethods())
                    .filter(m -> CHAT_COMPONENT.isAssignableFrom(m.getReturnType()))
                    .filter(m -> Arrays.equals(m.getParameterTypes(), new Class[]{String.class}))
                    .findFirst().orElseThrow(RuntimeException::new);

            CHAT_COMPONENT_TO_JSON = Arrays.stream(chatSerializer.getDeclaredMethods())
                    .filter(m -> m.getReturnType().equals(String.class))
                    .filter(m -> Arrays.equals(m.getParameterTypes(), new Class[]{CHAT_COMPONENT}))
                    .findAny().orElseThrow(RuntimeException::new);

            boolean hasComponentApi;
            try {
                ItemMeta.class.getMethod("setDisplayNameComponent", BaseComponent[].class);
                hasComponentApi = true;
            } catch (NoSuchMethodException e) {
                hasComponentApi = false;
            }
            HAS_COMPONENT_API = hasComponentApi;
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static ItemStack setDisplayNameComponents(BaseComponent[] components, ItemStack itemStack) throws ReflectiveOperationException {
        ItemMeta meta = itemStack.getItemMeta();
        if (HAS_COMPONENT_API) {
            meta.setDisplayNameComponent(components);
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        for (Field field : meta.getClass().getDeclaredFields()) {
            if (field.getType().equals(CHAT_COMPONENT)) {
                field.setAccessible(true);
                field.set(meta, CHAT_COMPONENT_FROM_JSON.invoke(null, ComponentSerializer.toString(components)));
                itemStack.setItemMeta(meta);
                return itemStack;
            }
        }
        return itemStack;
    }

    public static ItemStack setLoreComponents(List<BaseComponent[]> components, ItemStack itemStack) throws ReflectiveOperationException {
        ItemMeta meta = itemStack.getItemMeta();
        if (HAS_COMPONENT_API) {
            meta.setLoreComponents(components);
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        List<Object> chatBaseComponents = new ArrayList<>();
        for (BaseComponent[] c : components) {
            try {
                chatBaseComponents.add(CHAT_COMPONENT_FROM_JSON.invoke(null, ComponentSerializer.toString(c)));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        for (Field field : meta.getClass().getDeclaredFields()) {
            if (field.getType().equals(List.class) && ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].equals(CHAT_COMPONENT)) {
                field.setAccessible(true);
                field.set(meta, chatBaseComponents);
                itemStack.setItemMeta(meta);
                return itemStack;
            }
        }
        return itemStack;
    }

    static BaseComponent[] getTextComponent(Object obj, int count) throws ReflectiveOperationException {
        int i = 0;
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.getType().equals(CHAT_COMPONENT) && i++ == count) {
                field.setAccessible(true);
                return ComponentSerializer.parse((String) CHAT_COMPONENT_TO_JSON.invoke(null, field.get(obj)));
            }
        }
        throw new RuntimeException("Text component field not found");
    }
}
