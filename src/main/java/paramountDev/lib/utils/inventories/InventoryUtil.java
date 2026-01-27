package paramountDev.lib.utils.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import paramountDev.lib.utils.items.ItemUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static paramountDev.lib.utils.messages.MessageUtil.color;
import static paramountDev.lib.utils.messages.MessageUtil.sendMessageWithPrefix;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class InventoryUtil {
    public static Inventory create(String title, int rows, ItemStack... items) {
        if (rows < 1) rows = 1;
        if (rows > 6) rows = 6;

        Inventory inv = Bukkit.createInventory(null, rows * 9, color(title));

        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                if (i < inv.getSize() && items[i] != null) {
                    inv.setItem(i, items[i]);
                }
            }
        }
        return inv;
    }

    public static void fillEmptySlots(Inventory inv, ItemStack filler) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                inv.setItem(i, filler);
            }
        }
    }

    public static void fillBorder(Inventory inv, ItemStack filler) {
        int size = inv.getSize();
        int rows = size / 9;

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, filler);
            inv.setItem(size - 9 + i, filler);
        }

        for (int i = 1; i < rows - 1; i++) {
            inv.setItem(i * 9, filler);
            inv.setItem(i * 9 + 8, filler);
        }
    }

    public static Inventory createPaged(String title, List<ItemStack> allItems, int page, int rows) {
        if (rows < 2) rows = 2;
        if (rows > 6) rows = 6;

        int size = rows * 9;
        int navRowStart = size - 9;
        int itemsPerPage = navRowStart;

        int maxPages = (int) Math.ceil((double) allItems.size() / itemsPerPage);
        if (maxPages == 0) maxPages = 1;

        if (page < 1) page = 1;
        if (page > maxPages) page = maxPages;

        Inventory inv = Bukkit.createInventory(null, size, color(title + " &8| Стр. " + page));

        if (!allItems.isEmpty()) {
            int startIndex = (page - 1) * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, allItems.size());

            int slotIndex = 0;
            for (int i = startIndex; i < endIndex; i++) {
                inv.setItem(slotIndex, allItems.get(i));
                slotIndex++;
            }
        }

        ItemStack glass = ItemUtil.create(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        for (int i = navRowStart; i < size; i++) {
            inv.setItem(i, glass);
        }

        if (page > 1) {
            ItemStack prev = ItemUtil.create(Material.ARROW, "&d&l« Назад", Arrays.asList("&7Перейти на страницу " + (page - 1)));
            inv.setItem(navRowStart + 3, prev);
        }

        if (page < maxPages) {
            ItemStack next = ItemUtil.create(Material.ARROW, "&d&lВперед »", Arrays.asList("&7Перейти на страницу " + (page + 1)));
            inv.setItem(navRowStart + 5, next);
        }

        return inv;
    }

    public static void giveItemOrDrop(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            sendMessageWithPrefix(player, color("&cВаш инвентарь полон! Предмет выпал."));
        }
    }

    public static InventoryBuilder builder(String title, int rows) {
        return new InventoryBuilder(title, rows);
    }

    public static class InventoryBuilder implements InventoryHolder {
        private final Inventory inventory;
        private final Map<Integer, Consumer<InventoryClickEvent>> actions = new HashMap<>();
        private Consumer<InventoryOpenEvent> openAction;
        private Consumer<InventoryCloseEvent> closeAction;
        private boolean cancelClicks = true;

        public InventoryBuilder(String title, int rows) {
            if (rows < 1) rows = 1;
            if (rows > 6) rows = 6;
            this.inventory = Bukkit.createInventory(this, rows * 9, color(title));
        }

        public InventoryBuilder setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
            inventory.setItem(slot, item);
            if (action != null) {
                actions.put(slot, action);
            } else {
                actions.remove(slot);
            }
            return this;
        }

        public InventoryBuilder setItem(int slot, ItemStack item) {
            return setItem(slot, item, null);
        }

        public InventoryBuilder addItem(ItemStack item, Consumer<InventoryClickEvent> action) {
            int slot = inventory.firstEmpty();
            if (slot != -1) {
                setItem(slot, item, action);
            }
            return this;
        }

        public InventoryBuilder addItem(ItemStack item) {
            return addItem(item, null);
        }

        public InventoryBuilder fillEmpty(ItemStack filler) {
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                    setItem(i, filler, null);
                }
            }
            return this;
        }

        public InventoryBuilder fillBorder(ItemStack filler) {
            int size = inventory.getSize();
            int rows = size / 9;
            for (int i = 0; i < 9; i++) {
                setItem(i, filler);
                setItem(size - 9 + i, filler);
            }
            for (int i = 1; i < rows - 1; i++) {
                setItem(i * 9, filler);
                setItem(i * 9 + 8, filler);
            }
            return this;
        }

        public InventoryBuilder onOpen(Consumer<InventoryOpenEvent> action) {
            this.openAction = action;
            return this;
        }

        public InventoryBuilder onClose(Consumer<InventoryCloseEvent> action) {
            this.closeAction = action;
            return this;
        }

        public InventoryBuilder setCancelClicks(boolean cancel) {
            this.cancelClicks = cancel;
            return this;
        }

        public void open(Player player) {
            player.openInventory(inventory);
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }

        public void handleClick(InventoryClickEvent e) {
            if (cancelClicks) {
                e.setCancelled(true);
            }
            if (actions.containsKey(e.getSlot())) {
                actions.get(e.getSlot()).accept(e);
            }
        }

        public void handleOpen(InventoryOpenEvent e) {
            if (openAction != null) openAction.accept(e);
        }

        public void handleClose(InventoryCloseEvent e) {
            if (closeAction != null) closeAction.accept(e);
        }
    }
}
