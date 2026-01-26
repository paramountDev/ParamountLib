package paramountDev.lib.utils.blockDisplays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class BlockDisplayUtil {

    public static DisplayBuilder create(Location location, Material material) {
        return new DisplayBuilder(location, material.createBlockData());
    }

    public static DisplayBuilder create(Location location, BlockData blockData) {
        return new DisplayBuilder(location, blockData);
    }

    public static class DisplayBuilder {
        private final Location location;
        private final BlockData blockData;
        private Vector3f translation = new Vector3f(0, 0, 0);
        private Vector3f scale = new Vector3f(1, 1, 1);
        private Quaternionf leftRotation = new Quaternionf();
        private Quaternionf rightRotation = new Quaternionf();
        private Display.Billboard billboard = Display.Billboard.FIXED;
        private float brightness = -1;
        private float shadowRadius = 0f;
        private float shadowStrength = 1f;
        private int interpolationDuration = 0;
        private int interpolationDelay = 0;
        private boolean glowing = false;

        public DisplayBuilder(Location location, BlockData blockData) {
            this.location = location;
            this.blockData = blockData;
        }

        public DisplayBuilder scale(float x, float y, float z) {
            this.scale = new Vector3f(x, y, z);
            return this;
        }

        public DisplayBuilder translation(float x, float y, float z) {
            this.translation = new Vector3f(x, y, z);
            return this;
        }

        public DisplayBuilder rotation(float x, float y, float z, float angle) {
            this.leftRotation.rotateXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
            return this;
        }

        public DisplayBuilder billboard(Display.Billboard billboard) {
            this.billboard = billboard;
            return this;
        }

        public DisplayBuilder glow(boolean glowing) {
            this.glowing = glowing;
            return this;
        }

        public DisplayBuilder brightness(float brightness) {
            this.brightness = brightness;
            return this;
        }

        public DisplayBuilder interpolation(int duration, int delay) {
            this.interpolationDuration = duration;
            this.interpolationDelay = delay;
            return this;
        }

        public BlockDisplay build() {
            if (location.getWorld() == null) throw new IllegalStateException("World cannot be null");

            BlockDisplay display = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
            display.setBlock(blockData);
            display.setBillboard(billboard);
            display.setGlowing(glowing);

            Transformation transformation = new Transformation(translation, leftRotation, scale, rightRotation);
            display.setTransformation(transformation);

            if (brightness >= 0) {
                display.setBrightness(new Display.Brightness((int) brightness, (int) brightness));
            }

            display.setShadowRadius(shadowRadius);
            display.setShadowStrength(shadowStrength);
            display.setInterpolationDuration(interpolationDuration);
            display.setInterpolationDelay(interpolationDelay);

            return display;
        }
    }

    public static void updateTransformation(BlockDisplay display, Consumer<TransformationBuilder> consumer, int duration) {
        Transformation old = display.getTransformation();
        TransformationBuilder builder = new TransformationBuilder(old);
        consumer.accept(builder);

        display.setInterpolationDuration(duration);
        display.setInterpolationDelay(0);
        display.setTransformation(builder.build());
    }

    public static class TransformationBuilder {
        private Vector3f translation;
        private Vector3f scale;
        private Quaternionf leftRotation;
        private Quaternionf rightRotation;

        public TransformationBuilder(Transformation transformation) {
            this.translation = new Vector3f(transformation.getTranslation());
            this.scale = new Vector3f(transformation.getScale());
            this.leftRotation = new Quaternionf(transformation.getLeftRotation());
            this.rightRotation = new Quaternionf(transformation.getRightRotation());
        }

        public TransformationBuilder scale(float x, float y, float z) {
            this.scale = new Vector3f(x, y, z);
            return this;
        }

        public TransformationBuilder translation(float x, float y, float z) {
            this.translation = new Vector3f(x, y, z);
            return this;
        }

        public Transformation build() {
            return new Transformation(translation, leftRotation, scale, rightRotation);
        }
    }
}