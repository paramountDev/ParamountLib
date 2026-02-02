package paramountDev.lib.utils.models.handlers;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class FallbackModelHandler implements IModelHandler {
    @Override
    public boolean setModel(Entity entity, String modelId) {
        return false;
    }

    @Override
    public void removeModel(Entity entity) {
    }

    @Override
    public boolean playAnimation(Entity entity, String modelId, String animationId, boolean loop, float speed) {
        return false;
    }

    @Override
    public void stopAnimation(Entity entity, String modelId, String animationId) {
    }

    @Override
    public void updateAppearance(Entity entity, String modelId, boolean fixedBillboard, boolean enchanted, @Nullable Color tint) {
    }

    @Override
    public void stopAllAnimations(Entity entity, String modelId) {}

    @Override
    public long getAnimationDuration(String modelId, String animationId) {return 0;}
}
