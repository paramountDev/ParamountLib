package paramountDev.lib.utils.bossbars;


import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static paramountDev.lib.utils.messages.MessageUtil.color;

public class BossBarUtil {

    private static final Map<BossBar, BukkitTask> activeTasks = new HashMap<>();

    public static BossBar create(String title, BarColor color, BarStyle style, BarFlag... flags) {
        return Bukkit.createBossBar(color(title), color, style, flags);
    }

    public static BossBar create(Player player, String title, BarColor color, BarStyle style) {
        BossBar bar = create(title, color, style);
        bar.addPlayer(player);
        return bar;
    }

    public static void addAllPlayers(BossBar bar) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p);
        }
    }

    public static void remove(BossBar bar) {
        if (bar == null) return;
        stopTask(bar);
        bar.removeAll();
        bar.setVisible(false);
    }

    public static void startTimer(Plugin plugin, BossBar bar, int seconds, Runnable onFinish) {
        startTimer(plugin, bar, seconds, null, onFinish);
    }

    public static void startTimer(Plugin plugin, BossBar bar, int seconds, Consumer<Integer> onTick, Runnable onFinish) {
        stopTask(bar);

        bar.setProgress(1.0);

        BukkitTask task = new BukkitRunnable() {
            double timeLeft = seconds;
            final double step = 1.0 / (seconds * 20.0);

            @Override
            public void run() {
                double progress = bar.getProgress() - step;

                if (progress < 0) progress = 0;
                bar.setProgress(progress);

                timeLeft -= 0.05;
                if (onTick != null && (Math.abs(timeLeft - Math.round(timeLeft)) < 0.05)) {
                    onTick.accept((int) Math.round(timeLeft));
                }

                if (progress <= 0) {
                    if (onFinish != null) onFinish.run();
                    activeTasks.remove(bar);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        activeTasks.put(bar, task);
    }

    public static void startRainbow(Plugin plugin, BossBar bar, long speedTicks) {
        stopTask(bar);

        BarColor[] colors = BarColor.values();

        BukkitTask task = new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                bar.setColor(colors[index]);
                index++;
                if (index >= colors.length) index = 0;
            }
        }.runTaskTimer(plugin, 0L, speedTicks);

        activeTasks.put(bar, task);
    }

    public static void startBlink(Plugin plugin, BossBar bar, BarColor color1, BarColor color2, long speedTicks) {
        stopTask(bar);

        BukkitTask task = new BukkitRunnable() {
            boolean switchColor = false;

            @Override
            public void run() {
                bar.setColor(switchColor ? color1 : color2);
                switchColor = !switchColor;
            }
        }.runTaskTimer(plugin, 0L, speedTicks);

        activeTasks.put(bar, task);
    }

    private static void stopTask(BossBar bar) {
        if (activeTasks.containsKey(bar)) {
            activeTasks.get(bar).cancel();
            activeTasks.remove(bar);
        }
    }
}
