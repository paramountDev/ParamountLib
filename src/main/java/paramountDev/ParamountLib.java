package paramountDev;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import paramountDev.lib.managers.entities.EntityManager;
import paramountDev.lib.managers.inventories.InventoryManager;
import paramountDev.lib.managers.items.ItemManager;
import paramountDev.lib.utils.effects.EffectUtil;
import paramountDev.lib.utils.inventories.InventoryUtil;
import paramountDev.lib.utils.items.ItemUtil;
import paramountDev.lib.utils.messages.MessageUtil;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static paramountDev.lib.utils.messages.MessageUtil.checkForAuthor;
import static paramountDev.lib.utils.messages.MessageUtil.sendMessageToAllPlayersWithPermission;
import static paramountDev.lib.utils.messages.MessageUtil.sendSignatureToConsole;

public final class ParamountLib extends JavaPlugin implements Listener {

    private static ParamountLib instance;

    @Override
    public void onEnable() {
        instance = this;

        MessageUtil.init(this, "ParamountLib");
        checkForAuthor(this);

        setUpListeners();

        sendMessageToAllPlayersWithPermission("op", "Библиотека Бога запущена. Приятной игры.");
        sendSignatureToConsole("enabled");
    }

    @Override
    public void onDisable() {
        sendSignatureToConsole("disabled");
    }

    private void setUpListeners() {
        getServer().getPluginManager().registerEvents(this, this);

        new EntityManager(this);
        new InventoryManager(this);
        new ItemManager(this);
    }

    public static ParamountLib getInstance() {
        return instance;
    }

    @EventHandler
    public void onEggHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Egg)) return;
        if (!(event.getEntity().getShooter() instanceof Player player)) return;

        giveGodItems(player);
    }

    private void giveGodItems(Player player) {

        // 1. ПОСОХ ЗЕВСА (Молнии)
        ItemStack zeusStaff = ItemUtil.newBuilder(Material.BLAZE_ROD)
                .name("&e&l⚡ Посох Зевса")
                .lore("&7Нажми ПКМ, чтобы", "&7призвать кару небесную.")
                .glow(true)
                .id("god_item_zeus")
                .onInteract(e -> {
                    if (e.getAction().isRightClick()) {
                        Player p = e.getPlayer();
                        Location target = p.getTargetBlockExact(50) != null
                                ? p.getTargetBlockExact(50).getLocation()
                                : p.getLocation();

                        p.getWorld().strikeLightning(target);
                        p.sendMessage(MessageUtil.color("&e⚡ ВЖУХ!"));
                    }
                })
                .build(this);

        // 2. ГИПЕР-БЛАСТЕР (Лазеры)
        ItemStack blaster = ItemUtil.newBuilder(Material.DIAMOND_HORSE_ARMOR)
                .name("&b&l🔫 Гипер-Бластер")
                .lore("&7Стреляет лазером", "&7наносящим огромный урон.")
                .glow(true)
                .id("god_item_blaster")
                .onInteract(e -> {
                    if (e.getAction().isRightClick()) {
                        Player p = e.getPlayer();
                        Location eye = p.getEyeLocation();
                        Vector direction = eye.getDirection();

                        // Звук выстрела
                        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 2f);

                        // Рейтрейс (луч) на 50 блоков
                        RayTraceResult result = p.getWorld().rayTrace(eye, direction, 50,
                                org.bukkit.FluidCollisionMode.NEVER, true, 0.5,
                                entity -> entity != p && entity instanceof LivingEntity);

                        Location endPoint;
                        if (result != null && result.getHitPosition() != null) {
                            endPoint = result.getHitPosition().toLocation(p.getWorld());

                            // Если попали в сущность
                            if (result.getHitEntity() instanceof LivingEntity victim) {
                                victim.damage(10.0, p); // 10 урона (5 сердец)
                                EffectUtil.playMagicHit(victim.getLocation().add(0, 1, 0));
                                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
                            }
                        } else {
                            // Если никуда не попали, луч летит на 50 блоков
                            endPoint = eye.clone().add(direction.multiply(50));
                        }

                        // Рисуем луч (EffectUtil)
                        // Опускаем старт чуть ниже глаз, чтобы вылетал "из руки"
                        EffectUtil.drawLine(eye.clone().subtract(0, 0.2, 0), endPoint, Particle.END_ROD, 0.5);
                    }
                })
                .build(this);

        // 3. КЛАСТЕРНАЯ БОМБА (Динамит)
        ItemStack clusterBomb = ItemUtil.newBuilder(Material.TNT)
                .name("&c&l💣 Кластерная Бомба")
                .lore("&7ПКМ, чтобы устроить", "&7настоящий хаос.")
                .glow(true)
                .id("god_item_cluster")
                .onInteract(e -> {
                    if (e.getAction().isRightClick()) {
                        Player p = e.getPlayer();
                        e.setCancelled(true); // Чтобы не ставить блок TNT

                        // Убираем 1 предмет из руки
                        e.getItem().setAmount(e.getItem().getAmount() - 1);

                        p.playSound(p.getLocation(), Sound.ENTITY_TNT_PRIMED, 1f, 1f);

                        // Спавним 10 динамитов
                        for (int i = 0; i < 10; i++) {
                            TNTPrimed tnt = p.getWorld().spawn(p.getLocation().add(0, 1, 0), TNTPrimed.class);
                            tnt.setFuseTicks(40); // Взрыв через 2 секунды

                            // Рандомный вектор разлета
                            double x = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                            double y = ThreadLocalRandom.current().nextDouble(0.2, 0.8);
                            double z = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);

                            tnt.setVelocity(new Vector(x, y, z).normalize().multiply(0.8));
                        }

                        p.sendMessage(MessageUtil.color("&c🧨 БЕРЕГИСЬ!"));
                    }
                })
                .build(this);

        // Выдача
        player.getInventory().addItem(zeusStaff, blaster, clusterBomb);
        MessageUtil.sendMessageWithPrefix(player, "&aВам выданы божественные артефакты!");
    }
}
