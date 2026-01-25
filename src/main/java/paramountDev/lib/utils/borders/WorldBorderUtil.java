package paramountDev.lib.utils.borders;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.concurrent.ThreadLocalRandom;

public class WorldBorderUtil {

    public static void set(World world, double x, double z, double size) {
        WorldBorder border = world.getWorldBorder();
        border.setCenter(x, z);
        border.setSize(size);
    }

    public static void set(World world, double size) {
        WorldBorder border = world.getWorldBorder();
        border.setSize(size);
    }

    public static void animate(World world, double newSize, long seconds) {
        WorldBorder border = world.getWorldBorder();
        border.setSize(newSize, seconds);
    }

    public static void stopAnimation(World world) {
        WorldBorder border = world.getWorldBorder();
        border.setSize(border.getSize());
    }

    public static void setDamage(World world, double buffer, double amount) {
        WorldBorder border = world.getWorldBorder();
        border.setDamageBuffer(buffer);
        border.setDamageAmount(amount);
    }

    public static void setWarning(World world, int distance, int time) {
        WorldBorder border = world.getWorldBorder();
        border.setWarningDistance(distance);
        border.setWarningTime(time);
    }

    public static void reset(World world) {
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(60000000);
        border.setDamageBuffer(5);
        border.setDamageAmount(0.2);
        border.setWarningDistance(5);
        border.setWarningTime(15);
    }

    public static boolean isInside(Location location) {
        return location.getWorld().getWorldBorder().isInside(location);
    }

    public static Location getRandomLocationInside(World world) {
        WorldBorder border = world.getWorldBorder();
        Location center = border.getCenter();
        double size = border.getSize();
        double radius = size / 2.0;

        double minX = center.getX() - radius;
        double maxX = center.getX() + radius;
        double minZ = center.getZ() - radius;
        double maxZ = center.getZ() + radius;

        double x = ThreadLocalRandom.current().nextDouble(minX, maxX);
        double z = ThreadLocalRandom.current().nextDouble(minZ, maxZ);
        double y = world.getHighestBlockYAt((int) x, (int) z) + 1;

        return new Location(world, x, y, z);
    }

    public static String getStatus(World world) {
        WorldBorder border = world.getWorldBorder();
        return String.format("Center: %.0f, %.0f | Size: %.0f",
                border.getCenter().getX(),
                border.getCenter().getZ(),
                border.getSize());
    }
}
