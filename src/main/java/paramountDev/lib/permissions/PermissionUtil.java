package paramountDev.lib.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static paramountDev.lib.messages.MessageUtil.color;
import static paramountDev.lib.messages.MessageUtil.sendMessageWithPrefix;
import static paramountDev.lib.sounds.SoundUtil.playDeny;

public class PermissionUtil {

    private static final String DEFAULT_NO_PERM_MESSAGE = "У вас недостаточно прав.";

    public static boolean has(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    public static boolean isOp(CommandSender sender) {
        return sender.isOp() || sender.hasPermission("*");
    }

    public static boolean hasAny(CommandSender sender, String... permissions) {
        for (String perm : permissions) {
            if (sender.hasPermission(perm)) return true;
        }
        return false;
    }

    public static boolean hasAll(CommandSender sender, String... permissions) {
        for (String perm : permissions) {
            if (!sender.hasPermission(perm)) return false;
        }
        return true;
    }
    public static boolean check(CommandSender sender, String permission) {
        return check(sender, permission, DEFAULT_NO_PERM_MESSAGE);
    }

    public static boolean check(CommandSender sender, String permission, String customMessage) {
        if (sender.hasPermission(permission)) {
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            sendMessageWithPrefix(player, customMessage);
            playDeny(player);
        } else {
            sender.sendMessage(color(customMessage));
        }
        return false;
    }

    public static boolean checkAndPush(Player player, String permission) {
        if (player.hasPermission(permission)) return true;

        sendMessageWithPrefix(player, DEFAULT_NO_PERM_MESSAGE);
        playDeny(player);

        player.setVelocity(player.getLocation().getDirection().multiply(-0.5).setY(0.2));
        return false;
    }
}
