package paramountDev.lib.managers.blocks;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import paramountDev.lib.utils.holograms.HologramUtil;

import java.util.*;
import java.util.function.Consumer;

// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class BlockManager implements Listener {

    private static BlockManager instance;
    private final Plugin plugin;
    private final Map<String, CustomBlockData> registry = new HashMap<>();
    private final Map<Location, String> activeBlocks = new HashMap<>();
    private final Map<Location, List<TextDisplay>> holograms = new HashMap<>();

    public BlockManager(Plugin plugin) {
        this.plugin = plugin;
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static BlockManager getInstance() {
        return instance;
    }

    public void registerBlock(String id, CustomBlockData data) {
        registry.put(id, data);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.isCancelled()) return;

        ItemStack item = e.getItemInHand();
        String id = getCustomBlockId(item);

        if (id != null && registry.containsKey(id)) {
            CustomBlockData data = registry.get(id);
            Location loc = e.getBlock().getLocation();

            activeBlocks.put(loc, id);

            if (data.hologramLines != null && !data.hologramLines.isEmpty()) {
                double spacing = 0.25;
                double startY = 1.2 + (data.hologramLines.size() * spacing);
                Location holoLoc = loc.clone().add(0.5, startY, 0.5);

                List<TextDisplay> displays = HologramUtil.create(plugin, holoLoc, data.hologramLines, spacing);
                holograms.put(loc, displays);
            }

            if (data.placeAction != null) {
                data.placeAction.accept(e);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        Location loc = e.getBlock().getLocation();

        if (activeBlocks.containsKey(loc)) {
            String id = activeBlocks.get(loc);
            CustomBlockData data = registry.get(id);

            e.setDropItems(false);
            if (data.drops != null && !data.drops.isEmpty()) {
                for (ItemStack drop : data.drops) {
                    loc.getWorld().dropItemNaturally(loc, drop);
                }
            }

            if (holograms.containsKey(loc)) {
                HologramUtil.removeList(holograms.get(loc));
                holograms.remove(loc);
            }

            if (data.breakAction != null) {
                data.breakAction.accept(e);
            }

            activeBlocks.remove(loc);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getClickedBlock() == null) return;

        Location loc = e.getClickedBlock().getLocation();

        if (activeBlocks.containsKey(loc)) {
            String id = activeBlocks.get(loc);
            CustomBlockData data = registry.get(id);

            if (data != null && data.interactAction != null) {
                data.interactAction.accept(e);
            }
        }
    }

    public void updateHologram(Location loc, int lineIndex, String newText) {
        if (holograms.containsKey(loc)) {
            List<TextDisplay> displays = holograms.get(loc);
            if (lineIndex >= 0 && lineIndex < displays.size()) {
                HologramUtil.updateText(displays.get(lineIndex), newText);
            }
        }
    }

    private String getCustomBlockId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "pdev_custom_block_id");
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public static class CustomBlockData {
        public List<String> hologramLines;
        public List<ItemStack> drops;
        public Consumer<BlockPlaceEvent> placeAction;
        public Consumer<BlockBreakEvent> breakAction;
        public Consumer<PlayerInteractEvent> interactAction;
    }
}