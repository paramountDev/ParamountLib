package paramountDev.lib.utils.entities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import paramountDev.lib.managers.entities.EntityManager;
import paramountDev.lib.utils.projectiles.ProjectileUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

import static paramountDev.lib.utils.messages.MessageUtil.color;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class EntityUtil {

    public static final String KEY_DROPS = "pdev_custom_drops";
    public static final String KEY_DEATH_SOUND = "pdev_death_sound";
    public static final String KEY_KILL_XP = "pdev_kill_xp";
    public static final String KEY_ATTACK_DAMAGE = "pdev_attack_damage";
    public static final String KEY_ATTACK_EFFECTS = "pdev_attack_effects";
    public static final String KEY_ATTACK_ACTIONS = "pdev_attack_actions";
    public static final String KEY_BOSS_TITLE = "pdev_boss_title";
    public static final String KEY_BOSS_COLOR = "pdev_boss_color";
    public static final String KEY_BOSS_STYLE = "pdev_boss_style";
    public static final String KEY_BOSS_RADIUS = "pdev_boss_radius";

    public static final String KEY_HOLO_LINES = "pdev_holo_lines";
    public static final String KEY_HOLO_HEIGHT = "pdev_holo_height";
    public static final String HOLO_SEPARATOR = "|||";


    public static <T extends LivingEntity> MobBuilder<T> create(Location location, Class<T> clazz) {
        return new MobBuilder<>(location, clazz);
    }

    public static <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<T> consumer) {
        if (location == null || location.getWorld() == null) return null;
        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) return null;
        try {
            return location.getWorld().spawn(location, clazz, consumer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends Entity> T spawn(Location location, Class<T> clazz) {
        return spawn(location, clazz, e -> {
        });
    }

    public static void setData(Entity entity, Plugin plugin, String key, String value) {
        entity.getPersistentDataContainer().set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
    }

    public static class MobBuilder<T extends LivingEntity> {
        private final Location location;
        private final Class<T> clazz;
        private String name;
        private Double maxHealth;
        private Double damage;
        private Double speed;
        private ItemStack hand, offHand, helmet, chest, legs, boots;
        private boolean silent = false;
        private boolean noAi = false;
        private boolean glowing = false;
        private List<String> hologramLines;
        private double hologramHeight = 1.0;
        private String bossBarTitle;
        private BarColor bossBarColor;
        private BarStyle bossBarStyle;
        private double bossBarRadius = 20.0;
        private List<ItemStack> drops;
        private int expDrop = -1;
        private Sound deathSound;
        private float deathSoundVolume = 1f;
        private float deathSoundPitch = 1f;
        private String attackEffects = "";
        private Double customAttackDamage;
        private final List<String> actions = new ArrayList<>();

        private record MobTask<T>(int interval, Consumer<T> task) {
        }

        private final List<MobTask<T>> mobTasks = new ArrayList<>();
        private Consumer<T> deathHandler;

        public MobBuilder(Location location, Class<T> clazz) {
            this.location = location;
            this.clazz = clazz;
        }

        public MobBuilder<T> name(String name) {
            this.name = name;
            return this;
        }

        public MobBuilder<T> stats(double hp, double dmg, double speed) {
            this.maxHealth = hp;
            this.damage = dmg;
            this.speed = speed;
            return this;
        }

        public MobBuilder<T> equipment(ItemStack hand, ItemStack offHand, ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots) {
            this.hand = hand;
            this.offHand = offHand;
            this.helmet = helmet;
            this.chest = chest;
            this.legs = legs;
            this.boots = boots;
            return this;
        }

        public MobBuilder<T> addAttackEffect(PotionEffectType type, int durationTicks, int amplifier) {
            this.attackEffects += type.getName() + ";" + durationTicks + ";" + amplifier + "|";
            return this;
        }

        public MobBuilder<T> attackDamage(double damage) {
            this.customAttackDamage = damage;
            return this;
        }

        public MobBuilder<T> armor(ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots) {
            return equipment(this.hand, this.offHand, helmet, chest, legs, boots);
        }

        public MobBuilder<T> hand(ItemStack item) {
            this.hand = item;
            return this;
        }

        public MobBuilder<T> flags(boolean silent, boolean noAi, boolean glowing) {
            this.silent = silent;
            this.noAi = noAi;
            this.glowing = glowing;
            return this;
        }

        public MobBuilder<T> hologram(List<String> lines, double heightOffset) {
            this.hologramLines = lines;
            this.hologramHeight = heightOffset;
            return this;
        }

        public MobBuilder<T> bossBar(String title, BarColor color, BarStyle style, double radius) {
            this.bossBarTitle = title;
            this.bossBarColor = color;
            this.bossBarStyle = style;
            this.bossBarRadius = radius;
            return this;
        }

        public MobBuilder<T> drops(List<ItemStack> drops, int exp) {
            this.drops = drops;
            this.expDrop = exp;
            return this;
        }

        public MobBuilder<T> deathSound(Sound sound, float volume, float pitch) {
            this.deathSound = sound;
            this.deathSoundVolume = volume;
            this.deathSoundPitch = pitch;
            return this;
        }

        public MobBuilder<T> addAttackAction(String actionName) {
            this.actions.add(actionName);
            return this;
        }

        public MobBuilder<T> addDeathAction(Consumer<T> handler) {
            this.deathHandler = handler;
            return this;
        }

        public MobBuilder<T> addRepeatingTask(int intervalTicks, Consumer<T> task) {
            this.mobTasks.add(new MobTask<>(intervalTicks, task));
            return this;
        }

        public MobBuilder<T> addVanillaProjectileTask(int interval, Class<? extends Projectile> clazz, double speed) {
            return this.addRepeatingTask(interval, entity -> {
                if (entity instanceof Mob mob) {
                    LivingEntity target = mob.getTarget();
                    if (target != null && target.isValid()) {
                        ProjectileUtil.shootVanilla(entity, target, clazz, speed);
                        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_SKELETON_SHOOT, 1f, 1f);
                    }
                }
            });
        }

        public MobBuilder<T> addBlockProjectileTask(int interval, Material material, float scaleX, float scaleY, float scaleZ, double speed, Consumer<ProjectileUtil.ProjectileHitContext> onHit) {
            return this.addRepeatingTask(interval, entity -> {
                if (entity instanceof Mob mob) {
                    LivingEntity target = mob.getTarget();

                    if (target == null) {
                        target = entity.getNearbyEntities(15, 10, 15).stream()
                                .filter(e -> e instanceof Player)
                                .map(e -> (LivingEntity) e)
                                .findFirst().orElse(null);
                    }

                    if (target != null && target.isValid()) {
                        ProjectileUtil.shootBlock(entity, target, material, scaleX, scaleY, scaleZ, speed, onHit);
                    }
                }
            });
        }

        public T spawn(Plugin plugin) {
            T entity = EntityUtil.spawn(location, clazz);
            if (entity == null) return null;

            if (name != null) {
                entity.setCustomName(color(name));
                entity.setCustomNameVisible(true);
            }
            EntityUtil.setStats(entity, maxHealth, damage, speed);
            EntityUtil.setEquipment(entity, hand, offHand, helmet, chest, legs, boots);

            entity.setSilent(silent);
            entity.setAI(!noAi);
            entity.setGlowing(glowing);
            entity.setRemoveWhenFarAway(false);

            PersistentDataContainer pdc = entity.getPersistentDataContainer();

            if (drops != null && !drops.isEmpty()) {
                String serializedDrops = itemStackListToBase64(drops);
                pdc.set(new NamespacedKey(plugin, KEY_DROPS), PersistentDataType.STRING, serializedDrops);
            }

            if (deathSound != null) {
                String soundData = deathSound + ";" + deathSoundVolume + ";" + deathSoundPitch;
                pdc.set(new NamespacedKey(plugin, KEY_DEATH_SOUND), PersistentDataType.STRING, soundData);
            }

            if (expDrop >= 0) {
                pdc.set(new NamespacedKey(plugin, KEY_KILL_XP), PersistentDataType.INTEGER, expDrop);
            }

            if (bossBarTitle != null) {
                pdc.set(new NamespacedKey(plugin, KEY_BOSS_TITLE), PersistentDataType.STRING, bossBarTitle);
                pdc.set(new NamespacedKey(plugin, KEY_BOSS_COLOR), PersistentDataType.STRING, bossBarColor.name());
                pdc.set(new NamespacedKey(plugin, KEY_BOSS_STYLE), PersistentDataType.STRING, bossBarStyle.name());
                pdc.set(new NamespacedKey(plugin, KEY_BOSS_RADIUS), PersistentDataType.DOUBLE, bossBarRadius);
            }

            if (hologramLines != null && !hologramLines.isEmpty()) {
                String joinedLines = String.join(HOLO_SEPARATOR, hologramLines);
                pdc.set(new NamespacedKey(plugin, KEY_HOLO_LINES), PersistentDataType.STRING, joinedLines);
                pdc.set(new NamespacedKey(plugin, KEY_HOLO_HEIGHT), PersistentDataType.DOUBLE, hologramHeight);
            }

            if (!attackEffects.isEmpty()) {
                pdc.set(new NamespacedKey(plugin, KEY_ATTACK_EFFECTS), PersistentDataType.STRING, attackEffects);
            }

            if (customAttackDamage != null) {
                pdc.set(new NamespacedKey(plugin, KEY_ATTACK_DAMAGE), PersistentDataType.DOUBLE, customAttackDamage);
            }

            if (!actions.isEmpty()) {
                entity.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, KEY_ATTACK_ACTIONS),
                        PersistentDataType.STRING,
                        String.join("|", actions)
                );
            }

            if (deathHandler != null) {
                EntityManager.registerDeathCallback(entity.getUniqueId(), (Consumer<LivingEntity>) deathHandler);
            }

            for (MobTask<T> mobTask : mobTasks) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!entity.isValid() || entity.isDead()) {
                            this.cancel();
                            return;
                        }
                        mobTask.task().accept(entity);
                    }
                }.runTaskTimer(plugin, mobTask.interval(), mobTask.interval());
            }

            return entity;
        }
    }

    public static void setStats(LivingEntity entity, Double maxHealth, Double attackDamage, Double movementSpeed) {
        if (entity == null) return;
        if (maxHealth != null && entity.getAttribute(Attribute.MAX_HEALTH) != null) {
            entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
            entity.setHealth(maxHealth);
        }
        if (attackDamage != null && entity.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
            entity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(attackDamage);
        }
        if (movementSpeed != null && entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
            entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(movementSpeed);
        }
    }

    public static void setEquipment(LivingEntity entity, ItemStack hand, ItemStack offHand, ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots) {
        if (entity == null || entity.getEquipment() == null) return;
        if (hand != null) entity.getEquipment().setItemInMainHand(hand);
        if (offHand != null) entity.getEquipment().setItemInOffHand(offHand);
        if (helmet != null) entity.getEquipment().setHelmet(helmet);
        if (chest != null) entity.getEquipment().setChestplate(chest);
        if (legs != null) entity.getEquipment().setLeggings(legs);
        if (boots != null) entity.getEquipment().setBoots(boots);

        entity.getEquipment().setHelmetDropChance(0f);
        entity.getEquipment().setChestplateDropChance(0f);
        entity.getEquipment().setLeggingsDropChance(0f);
        entity.getEquipment().setBootsDropChance(0f);
        entity.getEquipment().setItemInMainHandDropChance(0f);
        entity.getEquipment().setItemInOffHandDropChance(0f);
    }

    public static String itemStackListToBase64(List<ItemStack> items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.size());
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static List<ItemStack> itemStackListFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            int size = dataInput.readInt();
            List<ItemStack> items = new java.util.ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                items.add((ItemStack) dataInput.readObject());
            }
            dataInput.close();
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}