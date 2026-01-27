package paramountDev.lib.utils.pdcs;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class PDCUtil {

    public static ItemStack setString(Plugin plugin, ItemStack item, String key, String value) {
        if (item == null || item.getType() == Material.AIR) return item;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        return item;
    }

    public static String getString(Plugin plugin, ItemStack item, String key) {
        if (item == null || item.getType() == Material.AIR) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(namespacedKey, PersistentDataType.STRING)) {
            return container.get(namespacedKey, PersistentDataType.STRING);
        }
        return null;
    }

    public static ItemStack setInt(Plugin plugin, ItemStack item, String key, int value) {
        if (item == null || item.getType() == Material.AIR) return item;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
        return item;
    }

    public static int getInt(Plugin plugin, ItemStack item, String key) {
        if (item == null || item.getType() == Material.AIR) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(namespacedKey, PersistentDataType.INTEGER)) {
            return container.get(namespacedKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

    public static ItemStack setDouble(Plugin plugin, ItemStack item, String key, double value) {
        if (item == null || item.getType() == Material.AIR) return item;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
        return item;
    }

    public static double getDouble(Plugin plugin, ItemStack item, String key) {
        if (item == null || item.getType() == Material.AIR) return 0.0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0.0;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(namespacedKey, PersistentDataType.DOUBLE)) {
            return container.get(namespacedKey, PersistentDataType.DOUBLE);
        }
        return 0.0;
    }

    public static boolean has(Plugin plugin, ItemStack item, String key) {
        if (item == null || item.getType() == Material.AIR) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING) ||
                meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.INTEGER) ||
                meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.DOUBLE);
    }

    public static ItemStack remove(Plugin plugin, ItemStack item, String key) {
        if (item == null || item.getType() == Material.AIR) return item;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        meta.getPersistentDataContainer().remove(namespacedKey);
        item.setItemMeta(meta);
        return item;
    }

    public static void setEntityString(Plugin plugin, Entity entity, String key, String value) {
        if (entity == null) return;
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        entity.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
    }

    public static String getEntityString(Plugin plugin, Entity entity, String key) {
        if (entity == null) return null;
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        if (entity.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) {
            return entity.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
        }
        return null;
    }
}