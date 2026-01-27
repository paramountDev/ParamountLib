package paramountDev.lib.utils.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import paramountDev.lib.managers.items.ItemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static paramountDev.lib.utils.messages.MessageUtil.color;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class ItemUtil {

    public static final String KEY_ITEM_ID = "pdev_item_id";

    public static ItemStack create(Material material, String name, List<String> lore, int amount, int modelData, boolean glow) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null) meta.setDisplayName(color(name));

            if (lore != null && !lore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) coloredLore.add(color(line));
                meta.setLore(coloredLore);
            }

            if (modelData > 0) meta.setCustomModelData(modelData);

            if (glow) {
                meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack create(Material material, String name, List<String> lore, int modelData) {
        return create(material, name, lore, 1, modelData, false);
    }

    public static ItemStack create(Material material, String name, List<String> lore) {
        return create(material, name, lore, 1, 0, false);
    }

    public static ItemStack createModelItem(Material material, String name, int modelData) {
        return create(material, name, null, 1, modelData, false);
    }

    public static ItemStack addGlow(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack getHead(Player player) {
        return getHead(player.getName());
    }

    public static ItemStack getHead(String playerName) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwner(playerName);
            meta.setDisplayName(color("&e" + playerName));
            head.setItemMeta(meta);
        }
        return head;
    }

    public static ItemStack getCustomHead(String owner, String name, List<String> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwner(owner);
            meta.setDisplayName(color(name));
            if (lore != null) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) coloredLore.add(color(line));
                meta.setLore(coloredLore);
            }
            head.setItemMeta(meta);
        }
        return head;
    }

    public static Builder newBuilder(Material material) {
        return new Builder(material);
    }

    public static class Builder {
        private final ItemStack item;
        private final ItemMeta meta;
        private String customId;

        // Хранилища действий
        private Consumer<PlayerInteractEvent> interactAction;
        private BiConsumer<Player, Entity> attackAction;
        private Consumer<BlockBreakEvent> breakAction;

        public Builder(Material material) {
            this.item = new ItemStack(material);
            this.meta = item.getItemMeta();
        }

        public Builder(ItemStack itemStack) {
            this.item = itemStack.clone();
            this.meta = item.getItemMeta();
        }

        public Builder name(String name) {
            if (meta != null) meta.setDisplayName(color(name));
            return this;
        }

        public Builder lore(String... lines) {
            if (meta != null) {
                List<String> lore = new ArrayList<>();
                for (String line : lines) lore.add(color(line));
                meta.setLore(lore);
            }
            return this;
        }

        public Builder lore(List<String> lines) {
            if (meta != null) {
                List<String> lore = new ArrayList<>();
                for (String line : lines) lore.add(color(line));
                meta.setLore(lore);
            }
            return this;
        }

        public Builder addLore(String line) {
            if (meta != null) {
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(color(line));
                meta.setLore(lore);
            }
            return this;
        }

        public Builder amount(int amount) {
            item.setAmount(amount);
            return this;
        }

        public Builder skullOwner(String playerName) {
            if (meta instanceof SkullMeta) {
                ((SkullMeta) meta).setOwner(playerName);
            }
            return this;
        }

        public Builder modelData(int data) {
            if (meta != null) meta.setCustomModelData(data);
            return this;
        }

        public Builder model(String namespace, String modelName) {
            if (meta != null)  meta.setItemModel(new NamespacedKey(namespace, modelName));
            return this;
        }

        public Builder glow(boolean glow) {
            if (meta != null && glow) {
                meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            return this;
        }

        public Builder flags(ItemFlag... flags) {
            if (meta != null) meta.addItemFlags(flags);
            return this;
        }

        public Builder unbreakable(boolean unbreakable) {
            if (meta != null) meta.setUnbreakable(unbreakable);
            return this;
        }

        public Builder editMeta(Consumer<ItemMeta> consumer) {
            if (meta != null) consumer.accept(meta);
            return this;
        }

        public Builder id(String id) {
            this.customId = id;
            return this;
        }

        public Builder onInteract(Consumer<PlayerInteractEvent> action) {
            this.interactAction = action;
            return this;
        }

        public Builder onAttack(BiConsumer<Player, Entity> action) {
            this.attackAction = action;
            return this;
        }

        public Builder onBreak(Consumer<BlockBreakEvent> action) {
            this.breakAction = action;
            return this;
        }

        public ItemStack build(Plugin plugin) {
            if (meta != null) {
                if (customId == null && (interactAction != null || attackAction != null || breakAction != null)) {
                    customId = "temp_" + UUID.randomUUID().toString();
                }
                if (customId != null) {
                    NamespacedKey key = new NamespacedKey(plugin, KEY_ITEM_ID);
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, customId);

                    // Регистрируем действия в ItemListener
                    if (interactAction != null) ItemManager.registerInteract(customId, interactAction);
                    if (attackAction != null) ItemManager.registerAttack(customId, attackAction);
                    if (breakAction != null) ItemManager.registerBreak(customId, breakAction);
                }

                item.setItemMeta(meta);
            }
            return item;
        }
    }
}
