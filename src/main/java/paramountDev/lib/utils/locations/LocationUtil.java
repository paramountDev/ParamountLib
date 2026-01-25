package paramountDev.lib.utils.locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import paramountDev.lib.utils.math.MathUtil;

import java.util.ArrayList;
import java.util.List;
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

    public static Location findRandomSafeLocation(World world, int radiusMin, int radiusMax) {
        for (int i = 0; i < 10; i++) {
            int x = MathUtil.getRandomInt(radiusMin, radiusMax);
            if (Math.random() > 0.5) x = -x;

            int z = MathUtil.getRandomInt(radiusMin, radiusMax);
            if (Math.random() > 0.5) z = -z;

            int y = world.getHighestBlockYAt(x, z);
            Block blockBelow = world.getBlockAt(x, y, z);
            Location loc = new Location(world, x, y + 1, z);

            if (blockBelow.getType().isSolid() && blockBelow.getType() != Material.WATER && blockBelow.getType() != Material.LAVA && !blockBelow.getType().name().contains("SLAB") && loc.getBlock().getType() == Material.AIR) {
                return loc;
            }
        }
        return null;

    }

    public static List<Block> getBlocksInRadius(Location center, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(center.clone().add(x, y, z).getBlock());
                }
            }
        }
        return blocks;
    }


}
