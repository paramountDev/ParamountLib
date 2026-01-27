package paramountDev.lib.managers.items;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import paramountDev.lib.utils.items.ItemUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class ItemManager implements Listener {

    private static ItemManager instance;
    private final Plugin plugin;

    private final Map<String, Consumer<PlayerInteractEvent>> interactActions = new HashMap<>();
    private final Map<String, BiConsumer<Player, Entity>> attackActions = new HashMap<>();
    private final Map<String, Consumer<BlockBreakEvent>> breakActions = new HashMap<>();

    public ItemManager(Plugin plugin) {
        this.plugin = plugin;
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void registerInteract(String id, Consumer<PlayerInteractEvent> action) {
        if (instance != null) instance.interactActions.put(id, action);
    }

    public static void registerAttack(String id, BiConsumer<Player, Entity> action) {
        if (instance != null) instance.attackActions.put(id, action);
    }

    public static void registerBreak(String id, Consumer<BlockBreakEvent> action) {
        if (instance != null) instance.breakActions.put(id, action);
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) return;

        ItemStack item = e.getItem();
        String id = getItemId(item);

        if (id != null && interactActions.containsKey(id)) {
            interactActions.get(id).accept(e);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        String id = getItemId(item);

        if (id != null && attackActions.containsKey(id)) {
            attackActions.get(id).accept(player, e.getEntity());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        String id = getItemId(item);

        if (id != null && breakActions.containsKey(id)) {
            breakActions.get(id).accept(e);
        }
    }

    private String getItemId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, ItemUtil.KEY_ITEM_ID);

        if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        }
        return null;
    }
}