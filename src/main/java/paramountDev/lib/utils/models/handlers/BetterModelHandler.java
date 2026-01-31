package paramountDev.lib.utils.models.handlers;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationIterator;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.tracker.TrackerUpdateAction;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class BetterModelHandler implements IModelHandler {

    @Override
    public boolean setModel(Entity entity, String modelId) {
        return BetterModel.model(modelId)
                .map(renderer -> renderer.getOrCreate(entity))
                .isPresent();
    }

    @Override
    public void removeModel(Entity entity) {
        BetterModel.registry(entity).ifPresent(registry -> registry.close());
    }

    @Override
    public boolean playAnimation(Entity entity, String modelId, String animationId, boolean loop, float speed) {
        AnimationModifier modifier = AnimationModifier.builder()
                .type(loop ? AnimationIterator.Type.LOOP : AnimationIterator.Type.PLAY_ONCE)
                .speed(speed)
                .build();

        var registryOpt = BetterModel.registry(entity);

        if (registryOpt.isPresent()) {
            var tracker = registryOpt.get().tracker(modelId);

            if (tracker != null) {
                tracker.animate(animationId, modifier);
                return true;
            }
        }
        return false;
    }

    @Override
    public void stopAnimation(Entity entity, String modelId, String animationId) {
    }

    @Override
    public void updateAppearance(Entity entity, String modelId, boolean fixedBillboard, boolean enchanted, @Nullable Color tint) {
        BetterModel.registry(entity)
                .map(reg -> reg.tracker(modelId))
                .ifPresent(tracker -> {
                    tracker.update(TrackerUpdateAction.billboard(
                            fixedBillboard ? Display.Billboard.FIXED : Display.Billboard.CENTER
                    ));

                    tracker.update(TrackerUpdateAction.enchant(enchanted));

                    if (tint != null) {
                        int rgb = (tint.getRed() << 16) | (tint.getGreen() << 8) | tint.getBlue();
                        tracker.update(TrackerUpdateAction.tint(rgb));
                    }
                });
    }
}
