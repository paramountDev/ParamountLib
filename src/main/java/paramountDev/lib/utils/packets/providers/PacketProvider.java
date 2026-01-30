package paramountDev.lib.utils.packets.providers;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public interface PacketProvider {
    void sendParticle(Player player, Particle particle, Location loc, int count, double offsetX, double offsetY, double offsetZ, double speed);

    void sendEntityAnimation(Player player, Entity entity, int animationId);

    void fakeEquipment(Player player, LivingEntity entity, EquipmentSlot slot, ItemStack item);

    void sendCameraShake(Player player);

    void setFakeGlow(Player player, Entity entity, boolean glowing);

    void sendFakeMessage(Player player, String senderName, String message);

    void sendFakeHealth(Player player, float health, int food, float saturation);

    void stopSound(Player player, String soundName);

    void hideEntity(Player player, Entity entity);
}
