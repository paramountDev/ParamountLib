package paramountDev.lib.utils.players;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static paramountDev.lib.utils.messages.MessageUtil.color;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class PlayerUtil {

    public static void forAll(Consumer<Player> action) {
        Bukkit.getOnlinePlayers().forEach(action);
    }

    public static void forMatch(Predicate<Player> condition, Consumer<Player> action) {
        Bukkit.getOnlinePlayers().stream().filter(condition).forEach(action);
    }

    public static List<Player> getMatching(Predicate<Player> condition) {
        return Bukkit.getOnlinePlayers().stream().filter(condition).collect(Collectors.toList());
    }

    public static long getCountInGameMode(GameMode mode) {
        return Bukkit.getOnlinePlayers().stream().filter(p -> p.getGameMode() == mode).count();
    }

    public static void restore(Player player) {
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH) != null
                ? player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() : 20.0;
        player.setHealth(maxHealth);
        player.setFoodLevel(20);
        player.setSaturation(5.0f);
        player.setFireTicks(0);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    public static void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItemInOffHand(null);
    }

    public static boolean hasSpace(Player player, ItemStack item) {
        return player.getInventory().firstEmpty() != -1;
    }

    public static void giveItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        } else {
            player.getInventory().addItem(item);
        }
    }

    public static void replaceItems(Player player, Material target, ItemStack newItem) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].getType() == target) {
                player.getInventory().setItem(i, newItem.clone());
            }
        }
    }

    public static boolean hasItem(Player player, Material material, int amount) {
        return player.getInventory().contains(material, amount);
    }

    public static void removeItems(Player player, Material material, int amount) {
        player.getInventory().removeItem(new ItemStack(material, amount));
    }

    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(color(message));
    }

    public static void sendTitle(Player player, String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(color(title), subtitle != null ? color(subtitle) : "", fadeIn, stay, fadeOut);
    }

    public static boolean isInsideRadius(Player player, org.bukkit.Location loc, double radius) {
        if (!player.getWorld().equals(loc.getWorld())) return false;
        return player.getLocation().distanceSquared(loc) <= (radius * radius);
    }

    public static MassActionBuilder mass() {
        return new MassActionBuilder(Bukkit.getOnlinePlayers());
    }

    public static class MassActionBuilder {
        private final Collection<? extends Player> targets;

        public MassActionBuilder(Collection<? extends Player> targets) {
            this.targets = targets;
        }

        public MassActionBuilder filter(Predicate<Player> condition) {
            return new MassActionBuilder(targets.stream().filter(condition).collect(Collectors.toList()));
        }

        public void sendMessage(String msg) {
            targets.forEach(p -> p.sendMessage(color(msg)));
        }

        public void playSound(org.bukkit.Sound sound, float vol, float pitch) {
            targets.forEach(p -> p.playSound(p.getLocation(), sound, vol, pitch));
        }

        public void teleport(org.bukkit.Location loc) {
            targets.forEach(p -> p.teleport(loc));
        }

        public void apply(Consumer<Player> action) {
            targets.forEach(action);
        }
    }
}