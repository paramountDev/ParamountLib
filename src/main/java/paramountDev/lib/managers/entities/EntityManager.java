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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import paramountDev.lib.utils.bossbars.BossBarUtil;
import paramountDev.lib.utils.entities.EntityUtil;
import paramountDev.lib.utils.sounds.SoundUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import static paramountDev.lib.utils.messages.MessageUtil.color;

public class EntityManager implements Listener {
    private final Plugin plugin;

    private final Map<UUID, BossBar> activeBossBars = new HashMap<>();
    private final Map<UUID, Double> bossRadii = new HashMap<>();

    private final Map<UUID, TextDisplay> activeHolograms = new HashMap<>();
    private final Map<UUID, Double> hologramHeights = new HashMap<>();

    private final Map<UUID, LivingEntity> activeBossEntities = new HashMap<>();

    public EntityManager(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startUpdateTask();
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
}