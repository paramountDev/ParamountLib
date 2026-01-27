package paramountDev.lib.utils.cooldowns;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class CooldownUtil {

    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();


    public static boolean tryCooldown(UUID uuid, String key, double seconds) {
        if (hasCooldown(uuid, key)) return false;
        setCooldown(uuid, key, seconds);
        return true;
    }

    public static void setCooldown(UUID uuid, String key, double seconds) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(key, System.currentTimeMillis() + (long) (seconds * 1000L));
    }

    public static boolean hasCooldown(UUID uuid, String key) {
        if (!cooldowns.containsKey(uuid)) return false;
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (!playerCooldowns.containsKey(key)) return false;
        if (System.currentTimeMillis() >= playerCooldowns.get(key)) {
            playerCooldowns.remove(key);
            return false;
        }
        return true;
    }

    public static boolean tryCooldown(Player player, String key, double seconds) {
        if (hasCooldown(player, key)) {
            return false;
        }
        setCooldown(player, key, seconds);
        return true;
    }

    public static void setCooldown(Player player, String key, double seconds) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(key, System.currentTimeMillis() + (long) (seconds * 1000L));
    }

    public static boolean hasCooldown(Player player, String key) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) return false;

        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (!playerCooldowns.containsKey(key)) return false;

        if (System.currentTimeMillis() >= playerCooldowns.get(key)) {
            playerCooldowns.remove(key);
            if (playerCooldowns.isEmpty()) {
                cooldowns.remove(uuid);
            }
            return false;
        }
        return true;
    }

    public static double getCooldownLeft(Player player, String key) {
        if (!hasCooldown(player, key)) return 0.0;

        long end = cooldowns.get(player.getUniqueId()).get(key);
        long left = end - System.currentTimeMillis();
        return Math.max(0, left / 1000.0);
    }

    public static String getCooldownFormatted(Player player, String key) {
        return String.format(Locale.US, "%.1fs", getCooldownLeft(player, key));
    }

    public static void removeCooldown(Player player, String key) {
        if (cooldowns.containsKey(player.getUniqueId())) {
            cooldowns.get(player.getUniqueId()).remove(key);
        }
    }

    public static void clearCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    public static void clearAll() {
        cooldowns.clear();
    }
}
