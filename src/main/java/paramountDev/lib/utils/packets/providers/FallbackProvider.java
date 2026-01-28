package paramountDev.lib.utils.packets.providers;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import paramountDev.ParamountLib;

import java.util.function.Consumer;

public class FallbackProvider implements PacketProvider {
    @Override
    public void sendParticle(Player player, Particle particle, Location loc, int count, double ox, double oy, double oz, double speed) {
        player.spawnParticle(particle, loc, count, ox, oy, oz, speed);
    }
    @Override public void sendEntityAnimation(Player player, Entity entity, int animationId) {}
    @Override public void fakeEquipment(Player player, LivingEntity entity, EquipmentSlot slot, ItemStack item) {}
    @Override public void sendCameraShake(Player player) {}
    @Override public void setFakeGlow(Player player, Entity entity, boolean glowing) { entity.setGlowing(glowing); }
    @Override public void sendFakeMessage(Player player, String senderName, String message) { player.sendMessage("§7[" + senderName + "] " + message); }
    @Override public void sendFakeHealth(Player player, float health, int food, float saturation) {}
    @Override public void stopSound(Player player, String soundName) { player.stopSound(soundName); }
    @Override public void hideEntity(Player player, Entity entity) { player.hideEntity(ParamountLib.getInstance(), entity); }
}