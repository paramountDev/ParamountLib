package paramountDev.lib.utils.models;

import org.bukkit.entity.Entity;
import paramountDev.lib.utils.models.handlers.BetterModelHandler;
import paramountDev.lib.utils.models.handlers.ModelHook;

import java.awt.*;

public class ModelUtil {

    public static void setModel(Entity entity, String modelId) {
        ModelHook.get().setModel(entity, modelId);
    }

    public static void removeModel(Entity entity) {
        ModelHook.get().removeModel(entity);
    }

    public static void animate(Entity entity, String modelId, String animId, boolean loop, float speed) {
        ModelHook.get().playAnimation(entity, modelId, animId, loop, speed);
    }

    public static void stopAnimation(Entity entity, String modelId, String animId) {
        ModelHook.get().stopAnimation(entity, modelId, animId);
    }

    public static void stopAllAnimations(Entity entity, String modelId) {
        if (ModelHook.get() instanceof BetterModelHandler handler) {
            handler.stopAllAnimations(entity, modelId);
        }
    }

    public static long getDuration(String modelId, String animId) {
        if (ModelHook.get() instanceof BetterModelHandler handler) {
            return handler.getAnimationDuration(modelId, animId);
        }
        return 2000;
    }

    public static void updateMeta(Entity entity, String modelId, boolean fixedBillboard, boolean enchanted) {
        ModelHook.get().updateAppearance(entity, modelId, fixedBillboard, enchanted, null);
    }

    public static void tint(Entity entity, String modelId, Color color) {
        ModelHook.get().updateAppearance(entity, modelId, false, false, color);
    }
}