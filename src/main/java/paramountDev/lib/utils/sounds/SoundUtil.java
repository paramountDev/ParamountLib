package paramountDev.lib.utils.sounds;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    public static void play(Player player, Sound sound, float volume, float pitch) {
        if (player == null || sound == null) return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void play(Player player, Sound sound) {
        play(player, sound, 1.0f, 1.0f);
    }

    public static void play(Location location, Sound sound) {
        play(location, sound, 1.0f, 1.0f);
    }

    public static void play(Location location, Sound sound, float volume, float pitch) {
        if (location == null || location.getWorld() == null || sound == null) return;
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public static void playAll(Sound sound, float volume, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            play(player, sound, volume, pitch);
        }
    }

    public static void play(Player player, String soundName, float volume, float pitch) {
        if (player == null || soundName == null) return;
        player.playSound(player.getLocation(), soundName, volume, pitch);
    }

    public static void play(Location location, String soundName, float volume, float pitch) {
        if (location == null || location.getWorld() == null || soundName == null) return;
        location.getWorld().playSound(location, soundName, volume, pitch);
    }

    public static void playSuccess(Player player) {
        play(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }

    public static void playError(Player player) {
        play(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1.0f, 1.0f);
    }

    public static void playClick(Player player) {
        play(player, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
    }

    public static void playPop(Player player) {
        play(player, Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }

    public static void playDing(Player player) {
        play(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
    }

    public static void playUiOpen(Player player) {
        play(player, Sound.BLOCK_ENDER_CHEST_OPEN, 0.8f, 1.2f);
    }

    public static void playUiClose(Player player) {
        play(player, Sound.BLOCK_ENDER_CHEST_CLOSE, 0.8f, 1.2f);
    }

    public static void playToggleOn(Player player) {
        play(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 2.0f);
    }

    public static void playToggleOff(Player player) {
        play(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 0.5f);
    }

    public static void playSelect(Player player) {
        play(player, Sound.UI_BUTTON_CLICK, 0.5f, 1.5f);
    }

    public static void playAlert(Player player) {
        play(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
    }

    public static void playDeny(Player player) {
        play(player, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    public static void playChallenge(Player player) {
        play(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }

    public static void playOrb(Player player) {
        play(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    public static void playTeleport(Player player) {
        play(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }

    public static void playMagicSuccess(Player player) {
        play(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.0f);
    }

    public static void playMagicFail(Player player) {
        play(player, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f);
    }

    public static void playUpgrade(Player player) {
        play(player, Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
    }

    public static void playRareDrop(Player player) {
        play(player, Sound.ITEM_TOTEM_USE, 1.0f, 1.5f);
    }

    public static void playBreak(Player player) {
        play(player, Sound.ENTITY_ITEM_BREAK, 1.0f, 0.8f);
    }

    public static void playEquip(Player player) {
        play(player, Sound.ITEM_ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
    }

    public static void playExplosion(Player player) {
        play(player, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
    }

    public static void playBass(Player player) {
        play(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
    }
}
