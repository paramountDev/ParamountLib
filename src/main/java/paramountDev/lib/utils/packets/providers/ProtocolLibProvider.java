package paramountDev.lib.utils.packets.providers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedParticle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtocolLibProvider implements PacketProvider {

    @Override
    public void sendParticle(Player player, Particle particle, Location loc, int count, double ox, double oy, double oz, double speed) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.WORLD_PARTICLES);
        packet.getNewParticles().write(0, WrappedParticle.create(particle, null));
        packet.getDoubles().write(0, loc.getX()).write(1, loc.getY()).write(2, loc.getZ());
        packet.getFloat().write(0, (float) ox).write(1, (float) oy).write(2, (float) oz).write(3, (float) speed);
        packet.getIntegers().write(0, count);
        packet.getBooleans().write(0, false);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public void sendEntityAnimation(Player player, Entity entity, int animationId) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ANIMATION);
        packet.getIntegers().write(0, entity.getEntityId()).write(1, animationId);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public void fakeEquipment(Player player, LivingEntity entity, EquipmentSlot slot, ItemStack item) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, entity.getEntityId());
        EnumWrappers.ItemSlot protocolSlot = switch (slot) {
            case OFF_HAND -> EnumWrappers.ItemSlot.OFFHAND;
            case HEAD -> EnumWrappers.ItemSlot.HEAD;
            case CHEST -> EnumWrappers.ItemSlot.CHEST;
            case LEGS -> EnumWrappers.ItemSlot.LEGS;
            case FEET -> EnumWrappers.ItemSlot.FEET;
            default -> EnumWrappers.ItemSlot.MAINHAND;
        };
        packet.getSlotStackPairLists().write(0, Collections.singletonList(new Pair<>(protocolSlot, item)));
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public void sendCameraShake(Player player) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.HURT_ANIMATION);
        packet.getIntegers().write(0, player.getEntityId());
        packet.getFloat().write(0, player.getLocation().getYaw());
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public void setFakeGlow(Player player, Entity entity, boolean glowing) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entity.getEntityId());

        byte mask = 0;
        if (glowing) mask |= 0x40;

        List<WrappedDataValue> dataValues = new ArrayList<>();

        var serializer = WrappedDataWatcher.Registry.get((java.lang.reflect.Type) Byte.class);

        dataValues.add(new WrappedDataValue(0, serializer, mask));
        packet.getDataValueCollectionModifier().write(0, dataValues);

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public void sendFakeMessage(Player player, String senderName, String message) {
        player.sendMessage("§7[" + senderName + "§7] §f" + message);
    }

    @Override
    public void sendFakeHealth(Player player, float health, int food, float saturation) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.UPDATE_HEALTH);
        packet.getFloat().write(0, health);
        packet.getIntegers().write(0, food);
        packet.getFloat().write(1, saturation);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public void stopSound(Player player, String soundName) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.STOP_SOUND);
        packet.getStrings().write(0, soundName);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public void hideEntity(Player player, Entity entity) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntLists().write(0, Collections.singletonList(entity.getEntityId()));
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }
}