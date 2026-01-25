package paramountDev.lib.containers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static paramountDev.lib.messages.MessageUtil.color;

public class ContainerUtil {

    public static void setBlock(Location location, Material material) {
        if (location != null && location.getWorld() != null) {
            location.getBlock().setType(material);
        }
    }
    public static void createContainer(Location location, Material material, String name, Map<Integer, ItemStack> items) {
        if (location == null || location.getWorld() == null) return;

        location.getBlock().setType(material);
        BlockState state = location.getBlock().getState();

        if (state instanceof Container) {
            Container container = (Container) state;

            if (name != null) {
                container.setCustomName(color(name));
            }

            if (items != null) {
                Inventory inv = container.getInventory();
                items.forEach((slot, item) -> {
                    if (slot >= 0 && slot < inv.getSize()) {
                        inv.setItem(slot, item);
                    }
                });
            }
            container.update();
        }
    }

    public static void createContainer(Location location, Material material, String name, List<ItemStack> items) {
        if (location == null || location.getWorld() == null) return;

        location.getBlock().setType(material);
        BlockState state = location.getBlock().getState();

        if (state instanceof Container) {
            Container container = (Container) state;

            if (name != null) {
                container.setCustomName(color(name));
            }

            if (items != null) {
                Inventory inv = container.getInventory();
                for (ItemStack item : items) {
                    if (item != null) {
                        inv.addItem(item);
                    }
                }
            }
            container.update();
        }
    }

    public static void dropItems(Location location, List<ItemStack> items) {
        if (location == null || location.getWorld() == null || items == null) return;
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                location.getWorld().dropItemNaturally(location, item);
            }
        }
    }

    public static List<ItemStack> getContainerContents(Location location) {
        if (location == null || location.getWorld() == null) return new ArrayList<>();

        BlockState state = location.getBlock().getState();
        if (state instanceof Container) {
            Container container = (Container) state;
            List<ItemStack> items = new ArrayList<>();
            for (ItemStack item : container.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    items.add(item);
                }
            }
            return items;
        }
        return new ArrayList<>();
    }

    public static void placeContainerOn(Location blockUnder, Material containerType, String name, List<ItemStack> items) {
        if (blockUnder == null || blockUnder.getWorld() == null) return;
        Location containerLoc = blockUnder.clone().add(0, 1, 0);
        createContainer(containerLoc, containerType, name, items);
    }


    public static void placeContainerWithFloor(Location containerLoc, Material containerType, String name, List<ItemStack> items, Material floorMaterial) {
        if (containerLoc == null || containerLoc.getWorld() == null) return;

        createContainer(containerLoc, containerType, name, items);

        Location floorLoc = containerLoc.clone().subtract(0, 1, 0);
        floorLoc.getBlock().setType(floorMaterial);
    }

    public static void placeContainerWithFloor(Location containerLoc, Material containerType, String name, Map<Integer, ItemStack> items, Material floorMaterial) {
        if (containerLoc == null || containerLoc.getWorld() == null) return;

        createContainer(containerLoc, containerType, name, items);

        Location floorLoc = containerLoc.clone().subtract(0, 1, 0);
        floorLoc.getBlock().setType(floorMaterial);
    }
}