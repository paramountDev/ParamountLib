package paramountDev.lib.holograms;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static paramountDev.lib.messages.MessageUtil.color;

public class HologramUtil {

    private static final String HOLO_KEY = "hologram_id";
    private static final String HOLO_VAL = "paramountDev_holo";

    public static List<TextDisplay> create(Plugin plugin, Location location, List<String> lines, double lineSpacing) {
        List<TextDisplay> displays = new ArrayList<>();
        if (lines == null || lines.isEmpty() || location == null || location.getWorld() == null) return displays;

        Location currentLoc = location.clone();

        for (String line : lines) {
            if (line.isEmpty() || line.equals(" ")) {
                currentLoc.subtract(0, lineSpacing, 0);
                continue;
            }

            TextDisplay td = currentLoc.getWorld().spawn(currentLoc, TextDisplay.class);
            applyDefaultSettings(td, plugin, line);

            displays.add(td);
            currentLoc.subtract(0, lineSpacing, 0);
        }
        return displays;
    }

    private static void applyDefaultSettings(TextDisplay td, Plugin plugin, String text) {
        td.setText(color(text));
        td.setBillboard(Display.Billboard.CENTER);
        td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        td.setShadowed(false);
        td.setSeeThrough(false);
        td.setAlignment(TextDisplay.TextAlignment.CENTER);

        NamespacedKey key = new NamespacedKey(plugin, HOLO_KEY);
        td.getPersistentDataContainer().set(key, PersistentDataType.STRING, HOLO_VAL);
    }

    public static void updateText(TextDisplay td, String newText) {
        if (td != null && td.isValid()) {
            td.setText(color(newText));
        }
    }

    public static void setScale(TextDisplay td, float scale) {
        if (td != null && td.isValid()) {
            Transformation transformation = td.getTransformation();
            Transformation newTransform = new Transformation(
                    transformation.getTranslation(),
                    transformation.getLeftRotation(),
                    new Vector3f(scale, scale, scale),
                    transformation.getRightRotation()
            );
            td.setTransformation(newTransform);
        }
    }

    public static void setBackground(TextDisplay td, Color color) {
        if (td != null && td.isValid()) {
            td.setBackgroundColor(color);
        }
    }

    public static void setShadow(TextDisplay td, boolean shadow) {
        if (td != null && td.isValid()) {
            td.setShadowed(shadow);
        }
    }

    public static void setTextOpacity(TextDisplay td, byte alpha) {
        if (td != null && td.isValid()) {
            td.setTextOpacity(alpha);
        }
    }

    public static void setBillboard(TextDisplay td, Display.Billboard billboard) {
        if (td != null && td.isValid()) {
            td.setBillboard(billboard);
        }
    }

    public static void teleportAll(List<TextDisplay> displays, Location newLocation, double lineSpacing) {
        if (displays == null || newLocation == null) return;

        Location currentLoc = newLocation.clone();
        for (TextDisplay td : displays) {
            if (td != null && td.isValid()) {
                td.teleport(currentLoc);
                currentLoc.subtract(0, lineSpacing, 0);
            }
        }
    }

    public static void setScaleAll(List<TextDisplay> displays, float scale) {
        if (displays == null) return;
        for (TextDisplay td : displays) {
            setScale(td, scale);
        }
    }

    public static void updateAllLines(List<TextDisplay> displays, List<String> newLines) {
        if (displays == null || newLines == null) return;

        int size = Math.min(displays.size(), newLines.size());
        for (int i = 0; i < size; i++) {
            updateText(displays.get(i), newLines.get(i));
        }
    }

    public static void removeAll(Plugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, HOLO_KEY);
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(TextDisplay.class)) {
                if (entity.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    entity.remove();
                    count++;
                }
            }
        }
        plugin.getLogger().info("Удалено " + count + " голограмм.");
    }

    public static void removeNearby(Plugin plugin, Location loc, double radius) {
        if (loc == null || loc.getWorld() == null) return;
        NamespacedKey key = new NamespacedKey(plugin, HOLO_KEY);

        for (Entity entity : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (entity instanceof TextDisplay) {
                if (entity.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    entity.remove();
                }
            }
        }
    }

    public static void removeList(List<TextDisplay> displays) {
        if (displays == null) return;
        for (TextDisplay td : displays) {
            if (td != null) td.remove();
        }
        displays.clear();
    }
}