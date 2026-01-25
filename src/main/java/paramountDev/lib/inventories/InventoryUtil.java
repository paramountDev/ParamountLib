package paramountDev.lib.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import paramountDev.lib.items.ItemUtil;

import java.util.Arrays;
import java.util.List;

import static paramountDev.lib.messages.MessageUtil.color;
import static paramountDev.lib.messages.MessageUtil.sendMessageWithPrefix;

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
}
