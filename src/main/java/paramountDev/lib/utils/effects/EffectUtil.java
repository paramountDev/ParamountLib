package paramountDev.lib.utils.effects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class EffectUtil {

    public static void spawnColored(Location loc, int r, int g, int b, float size) {
        if (loc.getWorld() == null) return;
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), size);
        loc.getWorld().spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dustOptions);
    }

    public static void spawnTransition(Location loc, Color from, Color to, float size) {
        if (loc.getWorld() == null) return;
        try {
            Particle.DustTransition transition = new Particle.DustTransition(from, to, size);
            loc.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, 1, 0, 0, 0, 0, transition);
        } catch (NoSuchFieldError | IllegalArgumentException e) {
            spawnColored(loc, from.getRed(), from.getGreen(), from.getBlue(), size);
        }
    }

    public static void drawLine(Location start, Location end, Particle particle, double step) {
        if (start.getWorld() == null || !start.getWorld().equals(end.getWorld())) return;

        double distance = start.distance(end);
        Vector p1 = start.toVector();
        Vector p2 = end.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(step);

        for (double covered = 0; covered < distance; p1.add(vector)) {
            start.getWorld().spawnParticle(particle, p1.getX(), p1.getY(), p1.getZ(), 1, 0, 0, 0, 0);
            covered += step;
        }
    }

    public static void drawCircle(Location center, float radius, Particle particle) {
        World world = center.getWorld();
        if (world == null) return;

        int points = (int) (radius * 20);
        double increment = (2 * Math.PI) / points;

        for (int i = 0; i < points; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            world.spawnParticle(particle, x, center.getY(), z, 1, 0, 0, 0, 0);
        }
    }

    public static void drawHelix(Location center, double height, double radius, Particle particle) {
        World world = center.getWorld();
        if (world == null) return;

        double y = 0;
        for (double t = 0; t <= height * 10; t += 0.5) {
            y = t / 10.0;
            if (y > height) break;

            double x = radius * Math.cos(t);
            double z = radius * Math.sin(t);

            world.spawnParticle(particle,
                    center.getX() + x,
                    center.getY() + y,
                    center.getZ() + z,
                    1, 0, 0, 0, 0);
        }
    }

    public static void drawSphere(Location center, double radius, Particle particle, int points) {
        World world = center.getWorld();
        if (world == null) return;

        double phi = Math.PI * (3.0 - Math.sqrt(5.0));

        for (int i = 0; i < points; i++) {
            double y = 1 - (i / (double) (points - 1)) * 2;
            double radiusAtY = Math.sqrt(1 - y * y);

            double theta = phi * i;

            double x = Math.cos(theta) * radiusAtY;
            double z = Math.sin(theta) * radiusAtY;

            world.spawnParticle(particle,
                    center.getX() + (x * radius),
                    center.getY() + (y * radius),
                    center.getZ() + (z * radius),
                    1, 0, 0, 0, 0);
        }
    }

    public static void playBlockBreak(Location loc, Material material) {
        if (loc.getWorld() == null) return;
        BlockData data = material.createBlockData();
        loc.getWorld().spawnParticle(Particle.BLOCK, loc, 20, 0.5, 0.5, 0.5, data);
    }

    public static void playItemBreak(Location loc, Material material) {
        if (loc.getWorld() == null) return;
        org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(material);
        loc.getWorld().spawnParticle(Particle.ITEM, loc, 15, 0.3, 0.3, 0.3, 0.1, stack);
    }

    public static void spawnGeyserPillar(Location loc, int height) {
        World w = loc.getWorld();
        if (w == null) return;

        for (int i = 0; i < height; i++) {
            w.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc.clone().add(0, i * 0.5, 0), 2, 0.1, 0.1, 0.1, 0.05);
        }
        w.spawnParticle(Particle.LAVA, loc, 50, 0.5, 2, 0.5, 0.5);
    }

    public static void spawnSplash(Location loc) {
        World w = loc.getWorld();
        if (w == null) return;
        w.spawnParticle(Particle.SPLASH, loc, 10, 0.3, 0.1, 0.3, 0.1);
    }

    public static void playBleed(Location loc) {
        spawnColored(loc.clone().add(0, 1, 0), 255, 0, 0, 1.5f);
        loc.getWorld().spawnParticle(Particle.BLOCK, loc.clone().add(0, 1, 0), 10, 0.2, 0.2, 0.2, Material.REDSTONE_BLOCK.createBlockData());
    }

    public static void playHeal(Location loc) {
        World w = loc.getWorld();
        if (w == null) return;
        w.spawnParticle(Particle.HEART, loc.clone().add(0, 2, 0), 5, 0.5, 0.5, 0.5);
        w.spawnParticle(Particle.COMPOSTER, loc.clone().add(0, 1, 0), 15, 0.5, 1, 0.5);
        drawCircle(loc, 1.0f, Particle.HAPPY_VILLAGER);
    }

    public static void playMagicHit(Location loc) {
        World w = loc.getWorld();
        if (w == null) return;
        w.spawnParticle(Particle.WITCH, loc.clone().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        w.spawnParticle(Particle.CRIT, loc.clone().add(0, 1, 0), 15, 0.5, 0.5, 0.5, 0.2);
    }

    public static void playExplosion(Location loc) {
        World w = loc.getWorld();
        if (w == null) return;
        w.spawnParticle(Particle.EXPLOSION, loc, 1);
        w.spawnParticle(Particle.SMOKE, loc, 50, 2, 2, 2, 0.1);
        w.spawnParticle(Particle.FLAME, loc, 50, 1, 1, 1, 0.2);
    }
    public static Location getBezierPoint(Location start, Location control, Location end, double t) {
        double invT = 1.0 - t;
        double x = invT * invT * start.getX() + 2 * invT * t * control.getX() + t * t * end.getX();
        double y = invT * invT * start.getY() + 2 * invT * t * control.getY() + t * t * end.getY();
        double z = invT * invT * start.getZ() + 2 * invT * t * control.getZ() + t * t * end.getZ();
        return new Location(start.getWorld(), x, y, z);
    }
    public static void playSummonFlash(Location loc) {
        World w = loc.getWorld();
        if (w == null) return;
        w.spawnParticle(Particle.FLASH, loc.clone().add(0, 1, 0), 3);
        w.spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        w.spawnParticle(Particle.SOUL, loc.clone().add(0, 0.5, 0), 20, 0.5, 1, 0.5, 0.05);
        drawCircle(loc.clone().add(0, 0.1, 0), 2.0f, Particle.SOUL_FIRE_FLAME);
    }
}