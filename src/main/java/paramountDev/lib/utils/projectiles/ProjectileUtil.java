package paramountDev.lib.utils.projectiles;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import paramountDev.lib.utils.blockDisplays.BlockDisplayUtil;
import paramountDev.lib.utils.effects.EffectUtil;

import java.util.function.Consumer;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class ProjectileUtil {

    public record ProjectileHitContext(LivingEntity shooter, LivingEntity victim, Location location) {
    }

    public static void shootBlock(LivingEntity shooter, LivingEntity target, Material material, float scaleX, float scaleY, float scaleZ, double speed, Consumer<ProjectileHitContext> onHit) {
        Plugin plugin = shooter.getServer().getPluginManager().getPlugins()[0];
        Location start = shooter.getEyeLocation();
        Vector dir = target.getEyeLocation().toVector().subtract(start.toVector()).normalize();

        BlockDisplay display = BlockDisplayUtil.create(start, material)
                .scale(scaleX, scaleY, scaleZ)
                .glow(true)
                .build();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > 100 || !display.isValid() || !shooter.isValid()) {
                    display.remove();
                    cancel();
                    return;
                }

                display.teleport(display.getLocation().add(dir.clone().multiply(speed)));
                EffectUtil.drawLine(display.getLocation(), display.getLocation(), Particle.CLOUD, 0.1);

                for (Entity entity : display.getNearbyEntities(0.6, 0.6, 0.6)) {
                    if (entity instanceof LivingEntity victim && !victim.equals(shooter)) {
                        onHit.accept(new ProjectileHitContext(shooter, victim, display.getLocation()));
                        display.remove();
                        cancel();
                        return;
                    }
                }

                if (display.getLocation().getBlock().getType().isSolid()) {
                    display.remove();
                    cancel();
                    return;
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    public static void shootVanilla(LivingEntity shooter, LivingEntity target, Class<? extends Projectile> projectileClass, double speed) {
        org.bukkit.entity.Projectile projectile = shooter.launchProjectile(projectileClass);
        Location start = shooter.getEyeLocation();
        Vector direction = target.getEyeLocation().toVector().subtract(start.toVector()).normalize();

        projectile.setVelocity(direction.multiply(speed));
    }
}
