package paramountDev.lib.locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Locale;

public class LocationUtil {

    public static String toString(Location loc) {
        if (loc == null || loc.getWorld() == null) return null;
        return loc.getWorld().getName() + ":" +
                String.format(Locale.US, "%.2f", loc.getX()) + ":" +
                String.format(Locale.US, "%.2f", loc.getY()) + ":" +
                String.format(Locale.US, "%.2f", loc.getZ()) + ":" +
                String.format(Locale.US, "%.2f", loc.getYaw()) + ":" +
                String.format(Locale.US, "%.2f", loc.getPitch());
    }

    public static String toBlockString(Location loc) {
        if (loc == null || loc.getWorld() == null) return null;
        return loc.getWorld().getName() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }

    public static Location fromString(String str) {
        if (str == null || str.isEmpty()) return null;
        try {
            String[] parts = str.split(":");
            if (parts.length < 4) return null;

            World world = Bukkit.getWorld(parts[0]);
            if (world == null) return null;

            double x = Double.parseDouble(parts[1].replace(",", "."));
            double y = Double.parseDouble(parts[2].replace(",", "."));
            double z = Double.parseDouble(parts[3].replace(",", "."));

            float yaw = 0;
            float pitch = 0;

            if (parts.length >= 6) {
                yaw = Float.parseFloat(parts[4].replace(",", "."));
                pitch = Float.parseFloat(parts[5].replace(",", "."));
            }

            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Location getCenter(Location loc) {
        if (loc == null) return null;
        Location center = loc.clone();
        center.setX(loc.getBlockX() + 0.5);
        center.setY(loc.getBlockY());
        center.setZ(loc.getBlockZ() + 0.5);
        return center;
    }

    public static Location getCenterBottom(Location loc) {
        if (loc == null) return null;
        Location center = loc.clone();
        center.setX(loc.getBlockX() + 0.5);
        center.setY(loc.getBlockY());
        center.setZ(loc.getBlockZ() + 0.5);
        return center;
    }
}
