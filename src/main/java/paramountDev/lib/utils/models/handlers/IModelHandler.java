package paramountDev.lib.utils.models.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public interface IModelHandler {

    boolean setModel(Entity entity, String modelId);
    void removeModel(Entity entity);
    boolean playAnimation(Entity entity, String modelId, String animationId, boolean loop, float speed);
    void stopAnimation(Entity entity, String modelId, String animationId);
    void updateAppearance(Entity entity, String modelId, boolean fixedBillboard, boolean enchanted, @Nullable Color tint);
    void stopAllAnimations(Entity entity, String modelId);
    long getAnimationDuration(String modelId, String animationId);
}
