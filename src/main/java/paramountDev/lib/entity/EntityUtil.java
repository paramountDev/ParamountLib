package paramountDev.lib.entity;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class EntityUtil {


    public static <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<T> consumer) {
        if (location == null || location.getWorld() == null) return null;

        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return null;
        }

        try {
            return location.getWorld().spawn(location, clazz, consumer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends Entity> T spawn(Location location, Class<T> clazz) {
        return spawn(location, clazz, e -> {});
    }

    public static void setStats(LivingEntity entity, Double maxHealth, Double attackDamage, Double movementSpeed) {
        if (entity == null) return;

        if (maxHealth != null) {
            if (entity.getAttribute(Attribute.MAX_HEALTH) != null) {
                entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
                entity.setHealth(maxHealth);
            }
        }

        if (attackDamage != null) {
            if (entity.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
                entity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(attackDamage);
            }
        }

        if (movementSpeed != null) {
            if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
                entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(movementSpeed);
            }
        }

        entity.setRemoveWhenFarAway(false);
    }

    public static void setEquipment(LivingEntity entity, ItemStack hand, ItemStack offHand, ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots) {
        if (entity == null || entity.getEquipment() == null) return;

        if (hand != null) entity.getEquipment().setItemInMainHand(hand);
        if (offHand != null) entity.getEquipment().setItemInOffHand(offHand);

        if (helmet != null) entity.getEquipment().setHelmet(helmet);
        if (chest != null) entity.getEquipment().setChestplate(chest);
        if (legs != null) entity.getEquipment().setLeggings(legs);
        if (boots != null) entity.getEquipment().setBoots(boots);

        entity.getEquipment().setItemInMainHandDropChance(0f);
        entity.getEquipment().setItemInOffHandDropChance(0f);
        entity.getEquipment().setHelmetDropChance(0f);
        entity.getEquipment().setChestplateDropChance(0f);
        entity.getEquipment().setLeggingsDropChance(0f);
        entity.getEquipment().setBootsDropChance(0f);
    }

    public static void clearEquipment(LivingEntity entity) {
        if (entity == null || entity.getEquipment() == null) return;
        entity.getEquipment().clear();
    }

    public static void makeDummy(Entity entity, boolean silent, boolean noAi, boolean invulnerable) {
        if (entity == null) return;

        entity.setSilent(silent);
        entity.setInvulnerable(invulnerable);

        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            living.setAI(!noAi);
            living.setRemoveWhenFarAway(false);
            living.setCollidable(false);
        }
    }
}