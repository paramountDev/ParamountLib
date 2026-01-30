package paramountDev.lib.utils.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import paramountDev.lib.managers.blocks.BlockManager;
import paramountDev.lib.utils.items.ItemUtil;
import paramountDev.lib.utils.messages.MessageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class BlockUtil {

    public static void destroyAndReturn(Plugin plugin,
                                        List<Block> blocks,
                                        long restoreDelayTicks,
                                        int breakSpeed,
                                        int restoreSpeed,
                                        Consumer<Block> onBreak,
                                        Consumer<Block> onRestore) {

        List<Block> validBlocks = new ArrayList<>();
        for (Block b : blocks) {
            if (b.getType() != Material.AIR && b.getType() != Material.BEDROCK && b.getType() != Material.BARRIER) {
                validBlocks.add(b);
            }
        }

        List<Map.Entry<Location, BlockData>> backup = new ArrayList<>();

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                for (int i = 0; i < breakSpeed; i++) {
                    if (index >= validBlocks.size()) {
                        this.cancel();
                        scheduleRestore(plugin, backup, restoreDelayTicks, restoreSpeed, onRestore);
                        return;
                    }

                    Block block = validBlocks.get(index);

                    backup.add(Map.entry(block.getLocation(), block.getBlockData()));

                    if (onBreak != null) {
                        onBreak.accept(block);
                    } else {
                        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1f);
                    }

                    block.setType(Material.AIR);
                    index++;
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public static void destroyAndReturn(Plugin plugin, List<Block> blocks, long restoreDelayTicks) {
        destroyAndReturn(plugin, blocks, restoreDelayTicks, 5, 5, null, null);
    }
    private static void scheduleRestore(Plugin plugin,
                                        List<Map.Entry<Location, BlockData>> backup,
                                        long delay,
                                        int speed,
                                        Consumer<Block> onRestore) {

        new BukkitRunnable() {
            @Override
            public void run() {
                backup.sort(Comparator.comparingDouble(entry -> entry.getKey().getY()));

                new BukkitRunnable() {
                    int index = 0;

                    @Override
                    public void run() {
                        for (int i = 0; i < speed; i++) {
                            if (index >= backup.size()) {
                                this.cancel();
                                return;
                            }

                            Map.Entry<Location, BlockData> entry = backup.get(index);
                            Location loc = entry.getKey();
                            BlockData data = entry.getValue();
                            Block block = loc.getBlock();

                            block.setBlockData(data);

                            if (onRestore != null) {
                                onRestore.accept(block);
                            } else {
                                loc.getWorld().playSound(loc, Sound.BLOCK_STONE_PLACE, 1f, 1f);
                            }

                            index++;
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            }
        }.runTaskLater(plugin, delay);
    }

    public static List<Block> getBlocksInSphere(Location center, int radius, boolean hollow) {
        List<Block> blocks = new ArrayList<>();
        int bX = center.getBlockX();
        int bY = center.getBlockY();
        int bZ = center.getBlockZ();

        for (int x = bX - radius; x <= bX + radius; x++) {
            for (int y = bY - radius; y <= bY + radius; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {
                    double distance = ((bX - x) * (bX - x) + ((bZ - z) * (bZ - z)) + ((bY - y) * (bY - y)));
                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        blocks.add(center.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
        }
        blocks.sort(Comparator.comparingDouble(b -> b.getLocation().distance(center)));
        return blocks;
    }

    public static List<Block> getBlocksInCuboid(Location loc1, Location loc2) {
        List<Block> blocks = new ArrayList<>();
        World world = loc1.getWorld();
        int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInCylinder(Location center, int radius, int height, boolean hollow) {
        List<Block> blocks = new ArrayList<>();
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();
        double rSq = radius * radius;
        double rHolSq = (radius - 1) * (radius - 1);

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                double distSq = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                if (distSq <= rSq && (!hollow || distSq >= rHolSq)) {
                    for (int y = cy; y < cy + height; y++) {
                        blocks.add(center.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksOnLine(Location start, Location end) {
        List<Block> blocks = new ArrayList<>();
        Vector vector = end.toVector().subtract(start.toVector());
        double length = vector.length();
        vector.normalize();

        for (double i = 0; i <= length; i += 0.5) {
            Vector pos = start.toVector().clone().add(vector.clone().multiply(i));
            Block b = start.getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            if (!blocks.contains(b)) {
                blocks.add(b);
            }
        }
        return blocks;
    }

    public static List<Block> getSurfaceBlocks(Location center, int radius) {
        List<Block> surface = new ArrayList<>();
        List<Block> sphere = getBlocksInSphere(center, radius, false);

        for (Block b : sphere) {
            if (b.getType().isSolid() && b.getRelative(BlockFace.UP).getType() == Material.AIR) {
                surface.add(b);
            }
        }
        return surface;
    }

    public static void fill(List<Block> blocks, Material material) {
        for (Block b : blocks) {
            b.setType(material);
        }
    }

    public static void replace(List<Block> blocks, Material from, Material to) {
        for (Block b : blocks) {
            if (b.getType() == from) {
                b.setType(to);
            }
        }
    }

    public static FallingBlock turnToFallingBlock(Block block) {
        if (block.getType() == Material.AIR) return null;

        BlockData data = block.getBlockData();
        block.setType(Material.AIR);

        return block.getWorld().spawnFallingBlock(
                block.getLocation().add(0.5, 0, 0.5),
                data
        );
    }

    public static Block getHighestSolidBlock(Location loc) {
        return loc.getWorld().getHighestBlockAt(loc);
    }

    public static boolean isSafeLocation(Location loc) {
        Block feet = loc.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        Block ground = feet.getRelative(BlockFace.DOWN);

        return !ground.getType().isAir() && ground.getType().isSolid()
                && feet.getType() == Material.AIR
                && head.getType() == Material.AIR;
    }

    public static int countBlocks(List<Block> blocks, Material type) {
        int count = 0;
        for (Block b : blocks) {
            if (b.getType() == type) count++;
        }
        return count;
    }

    public static Location getRandomLocationInRadius(Location center, int radius) {
        Random r = new Random();
        double x = (r.nextDouble() * radius * 2) - radius;
        double z = (r.nextDouble() * radius * 2) - radius;
        Location randomLoc = center.clone().add(x, 0, z);
        return randomLoc.getWorld().getHighestBlockAt(randomLoc).getLocation().add(0, 1, 0);
    }

    public static boolean isLiquid(Block block) {
        return block.getType() == Material.WATER || block.getType() == Material.LAVA;
    }

    public static Location getCenter(Block block) {
        return block.getLocation().add(0.5, 0.5, 0.5);
    }

    public static boolean isSurrounded(Block block) {
        return block.getRelative(BlockFace.NORTH).getType().isSolid() &&
                block.getRelative(BlockFace.SOUTH).getType().isSolid() &&
                block.getRelative(BlockFace.EAST).getType().isSolid() &&
                block.getRelative(BlockFace.WEST).getType().isSolid();
    }

    public static class BlockBuilder {

        private final ItemStack itemStack;
        private final ItemMeta meta;
        private final String id;

        private final BlockManager.CustomBlockData blockData;

        public BlockBuilder(String id, Material material) {
            this.id = id;
            this.itemStack = new ItemStack(material);
            this.meta = itemStack.getItemMeta();
            this.blockData = new BlockManager.CustomBlockData();
        }

        public static BlockBuilder create(String id, Material material) {
            return new BlockBuilder(id, material);
        }


        public BlockBuilder name(String name) {
            if (meta != null) meta.setDisplayName(MessageUtil.color(name));
            return this;
        }

        public BlockBuilder lore(String... lore) {
            if (meta != null) {
                List<String> list = new ArrayList<>();
                for (String s : lore) list.add(paramountDev.lib.utils.messages.MessageUtil.color(s));
                meta.setLore(list);
            }
            return this;
        }

        public BlockBuilder modelData(int data) {
            if (meta != null) meta.setCustomModelData(data);
            return this;
        }

        public BlockBuilder glow(boolean glow) {
            if (glow) {
                ItemUtil.addGlow(itemStack);
            }
            return this;
        }

        public BlockBuilder hologram(String... lines) {
            this.blockData.hologramLines = Arrays.asList(lines);
            return this;
        }

        public BlockBuilder hologram(List<String> lines) {
            this.blockData.hologramLines = lines;
            return this;
        }

        public BlockBuilder drops(ItemStack... items) {
            this.blockData.drops = new ArrayList<>(Arrays.asList(items));
            return this;
        }

        public BlockBuilder addDrop(ItemStack item) {
            if (this.blockData.drops == null) this.blockData.drops = new ArrayList<>();
            this.blockData.drops.add(item);
            return this;
        }

        public BlockBuilder onInteract(Consumer<PlayerInteractEvent> action) {
            this.blockData.interactAction = action;
            return this;
        }

        public BlockBuilder onBreak(Consumer<BlockBreakEvent> action) {
            this.blockData.breakAction = action;
            return this;
        }

        public BlockBuilder onPlace(Consumer<BlockPlaceEvent> action) {
            this.blockData.placeAction = action;
            return this;
        }

        public ItemStack build(Plugin plugin) {
            if (meta != null) {
                NamespacedKey key = new NamespacedKey(plugin, "pdev_custom_block_id");
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
                itemStack.setItemMeta(meta);
            }
            BlockManager.getInstance().registerBlock(id, blockData);

            return itemStack;
        }
    }

}