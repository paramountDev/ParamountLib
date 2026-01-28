package paramountDev.lib.utils.packets;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import paramountDev.lib.utils.messages.MessageUtil;
import paramountDev.lib.utils.packets.providers.FallbackProvider;
import paramountDev.lib.utils.packets.providers.PacketProvider;
import paramountDev.lib.utils.packets.providers.ProtocolLibProvider;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class ProtocolLibUtil {

    private static PacketProvider provider;

    static {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            try {
                provider = new ProtocolLibProvider();
                MessageUtil.sendMessageToConsole("[ParamountLib] ProtocolLib engine started.");
            } catch (Throwable e) {
                provider = new FallbackProvider();
                MessageUtil.sendMessageToConsole("[ParamountLib] ProtocolLib error, using Fallback.");
            }
        } else {
            provider = new FallbackProvider();
        }
    }

    public static void sendParticle(Player p, Particle particle, Location loc, int count) {
        provider.sendParticle(p, particle, loc, count, 0, 0, 0, 0);
    }

    public static void showFakeItem(Player p, LivingEntity entity, EquipmentSlot slot, ItemStack item) {
        provider.fakeEquipment(p, entity, slot, item);
    }

    public static void playAnimation(Player p, Entity entity, int animationId) {
        provider.sendEntityAnimation(p, entity, animationId);
    }

    public static void sendCameraShake(Player player) {
        provider.sendCameraShake(player);
    }

    public static void setGlow(Player p, Entity target, boolean active) {
        provider.setFakeGlow(p, target, active);
    }

    public static void sendFakeMessage(Player p, String from, String text) {
        provider.sendFakeMessage(p, from, text);
    }

    public static void setFakeHealth(Player p, float hp) {
        provider.sendFakeHealth(p, hp, 20, 5f);
    }

    public static void hideEntity(Player p, Entity target) {
        provider.hideEntity(p, target);
    }
}