package paramountDev.lib.managers.entities;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import paramountDev.lib.utils.bossbars.BossBarUtil;
import paramountDev.lib.utils.entities.EntityUtil;
import paramountDev.lib.utils.sounds.SoundUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static paramountDev.lib.utils.messages.MessageUtil.color;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class EntityManager implements Listener {
    private final Plugin plugin;

    private final Map<UUID, BossBar> activeBossBars = new HashMap<>();
    private final Map<UUID, Double> bossRadii = new HashMap<>();

    private final Map<UUID, TextDisplay> activeHolograms = new HashMap<>();
    private final Map<UUID, Double> hologramHeights = new HashMap<>();

    private final Map<UUID, LivingEntity> activeBossEntities = new HashMap<>();

    private static final Map<String, BiConsumer<LivingEntity, LivingEntity>> actionRegistry = new HashMap<>();
    private static final Map<UUID, Consumer<LivingEntity>> deathCallbacks = new HashMap<>();

    private static boolean isProcessingAction = false;
    public EntityManager(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startUpdateTask();

        registerDefaultActions();
    }

    public static void registerAction(String name, BiConsumer<LivingEntity, LivingEntity> action) {
        actionRegistry.put(name.toLowerCase(), action);
    }

    private void registerDefaultActions() {
        registerAction("toss_up", (attacker, victim) -> {
            victim.setVelocity(new Vector(0, 0.8, 0));
        });

        registerAction("knockback_huge", (attacker, victim) -> {
            Vector dir = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();
            victim.setVelocity(dir.multiply(2.0).setY(0.3));
        });

        registerAction("visual_explode", (attacker, victim) -> {
            victim.getWorld().createExplosion(victim.getLocation(), 0f, false, false);
        });
    }

    private void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (activeBossEntities.isEmpty()) return;

            for (UUID uuid : activeBossEntities.keySet()) {
                LivingEntity entity = activeBossEntities.get(uuid);

                if (entity == null || !entity.isValid()) {
                    removeBossData(uuid);
                    continue;
                }

                if (activeBossBars.containsKey(uuid)) {
                    updateBossBar(uuid, entity);
                }

                if (activeHolograms.containsKey(uuid)) {
                    updateHologram(uuid, entity);
                }
            }
        }, 1, 1);
    }

    private void updateBossBar(UUID uuid, LivingEntity entity) {
        BossBar bar = activeBossBars.get(uuid);
        Double radius = bossRadii.getOrDefault(uuid, 20.0);

        double maxHp = entity.getAttribute(Attribute.MAX_HEALTH).getValue();
        double hp = entity.getHealth();
        bar.setProgress(Math.max(0.0, Math.min(1.0, hp / maxHp)));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(entity.getWorld()) && player.getLocation().distance(entity.getLocation()) <= radius) {
                bar.addPlayer(player);
            } else {
                bar.removePlayer(player);
            }
        }
    }

    private void updateHologram(UUID uuid, LivingEntity entity) {
        TextDisplay display = activeHolograms.get(uuid);
        double height = hologramHeights.getOrDefault(uuid, 2.0);

        if (display != null && display.isValid()) {
            display.teleport(entity.getLocation().add(0, height, 0));
        }
    }

    @EventHandler
    public void onCustomMobDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        UUID uuid = entity.getUniqueId();

        if (deathCallbacks.containsKey(uuid)) {
            deathCallbacks.get(uuid).accept(entity);
            deathCallbacks.remove(uuid);
        }

        NamespacedKey dropsKey = new NamespacedKey(plugin, EntityUtil.KEY_DROPS);
        if (pdc.has(dropsKey, PersistentDataType.STRING)) {
            e.getDrops().clear();
            String data = pdc.get(dropsKey, PersistentDataType.STRING);
            List<ItemStack> customDrops = EntityUtil.itemStackListFromBase64(data);
            if (customDrops != null) e.getDrops().addAll(customDrops);
        }

        NamespacedKey xpKey = new NamespacedKey(plugin, EntityUtil.KEY_KILL_XP);
        if (pdc.has(xpKey, PersistentDataType.INTEGER)) {
            e.setDroppedExp(pdc.get(xpKey, PersistentDataType.INTEGER));
        }

        NamespacedKey soundKey = new NamespacedKey(plugin, EntityUtil.KEY_DEATH_SOUND);
        if (pdc.has(soundKey, PersistentDataType.STRING)) {
            try {
                String[] parts = pdc.get(soundKey, PersistentDataType.STRING).split(";");
                SoundUtil.play(entity.getLocation(), Sound.valueOf(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
            } catch (Exception ignored) {}
        }

        removeBossData(entity.getUniqueId());
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (e.getEntity().isValid()) {
                    checkAndLoadBossData((LivingEntity) e.getEntity());
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            if (entity instanceof LivingEntity) {
                checkAndLoadBossData((LivingEntity) entity);
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            removeBossData(entity.getUniqueId());
        }
    }


    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if (isProcessingAction) return;

        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        var pdc = attacker.getPersistentDataContainer();

        NamespacedKey dmgKey = new NamespacedKey(plugin, EntityUtil.KEY_ATTACK_DAMAGE);
        if (pdc.has(dmgKey, PersistentDataType.DOUBLE)) {
            if(pdc.get(dmgKey, PersistentDataType.DOUBLE) != null) {
                event.setDamage(pdc.get(dmgKey, PersistentDataType.DOUBLE));
            }
        }

        NamespacedKey effectKey = new NamespacedKey(plugin, EntityUtil.KEY_ATTACK_EFFECTS);
        if (pdc.has(effectKey, PersistentDataType.STRING)) {
            String rawEffects = pdc.get(effectKey, PersistentDataType.STRING);
            if (rawEffects != null) {
                for (String entry : rawEffects.split("\\|")) {
                    String[] parts = entry.split(";");
                    if (parts.length == 3) {
                        PotionEffectType type = PotionEffectType.getByName(parts[0]);
                        if (type != null) {
                            victim.addPotionEffect(new PotionEffect(type,
                                    Integer.parseInt(parts[1]),
                                    Integer.parseInt(parts[2])));
                        }
                    }
                }
            }
        }
        NamespacedKey actionsKey = new NamespacedKey(plugin, EntityUtil.KEY_ATTACK_ACTIONS);

        if (pdc.has(actionsKey, PersistentDataType.STRING)) {
            String rawActions = pdc.get(actionsKey, PersistentDataType.STRING);
            if (rawActions != null) {

                isProcessingAction = true;

                try {
                    for (String actionName : rawActions.split("\\|")) {
                        var action = actionRegistry.get(actionName.toLowerCase());
                        if (action != null) action.accept(attacker, victim);
                    }
                } finally {
                    isProcessingAction = false;
                }
            }
        }
    }

    private void checkAndLoadBossData(LivingEntity entity) {
        if (activeBossEntities.containsKey(entity.getUniqueId())) return;

        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        boolean isBoss = false;

        NamespacedKey titleKey = new NamespacedKey(plugin, EntityUtil.KEY_BOSS_TITLE);
        if (pdc.has(titleKey, PersistentDataType.STRING)) {
            String title = pdc.get(titleKey, PersistentDataType.STRING);
            String colorName = pdc.get(new NamespacedKey(plugin, EntityUtil.KEY_BOSS_COLOR), PersistentDataType.STRING);
            String styleName = pdc.get(new NamespacedKey(plugin, EntityUtil.KEY_BOSS_STYLE), PersistentDataType.STRING);
            Double radius = pdc.get(new NamespacedKey(plugin, EntityUtil.KEY_BOSS_RADIUS), PersistentDataType.DOUBLE);

            BossBar bar = BossBarUtil.create(title, BarColor.valueOf(colorName), BarStyle.valueOf(styleName));
            activeBossBars.put(entity.getUniqueId(), bar);
            bossRadii.put(entity.getUniqueId(), radius != null ? radius : 20.0);
            isBoss = true;
        }

        NamespacedKey holoKey = new NamespacedKey(plugin, EntityUtil.KEY_HOLO_LINES);
        if (pdc.has(holoKey, PersistentDataType.STRING)) {
            String joinedLines = pdc.get(holoKey, PersistentDataType.STRING);
            Double height = pdc.get(new NamespacedKey(plugin, EntityUtil.KEY_HOLO_HEIGHT), PersistentDataType.DOUBLE);

            String[] lines = joinedLines.split(Pattern.quote(EntityUtil.HOLO_SEPARATOR));
            StringBuilder finalText = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                finalText.append(color(lines[i]));
                if (i < lines.length - 1) finalText.append("\n");
            }

            TextDisplay display = entity.getWorld().spawn(entity.getLocation().add(0, height, 0), TextDisplay.class);
            display.setText(finalText.toString());
            display.setBillboard(Display.Billboard.CENTER);
            display.setBackgroundColor(Color.fromARGB(0,0,0,0));

            activeHolograms.put(entity.getUniqueId(), display);
            hologramHeights.put(entity.getUniqueId(), height != null ? height : 2.0);
            isBoss = true;
        }

        if (isBoss) {
            activeBossEntities.put(entity.getUniqueId(), entity);
        }
    }

    private void removeBossData(UUID uuid) {
        activeBossEntities.remove(uuid);

        if (activeBossBars.containsKey(uuid)) {
            BossBarUtil.remove(activeBossBars.get(uuid));
            activeBossBars.remove(uuid);
            bossRadii.remove(uuid);
        }

        if (activeHolograms.containsKey(uuid)) {
            TextDisplay display = activeHolograms.get(uuid);
            if (display != null) display.remove();
            activeHolograms.remove(uuid);
            hologramHeights.remove(uuid);
        }
    }

    public static void registerDeathCallback(UUID uuid, Consumer<LivingEntity> callback) {
        deathCallbacks.put(uuid, callback);
    }
}