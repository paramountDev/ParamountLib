package paramountDev.lib.managers.players;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import paramountDev.lib.utils.players.PlayerUtil;

public class PlayerManager implements Listener {

    private final Plugin plugin;
    private static final String FREEZE_KEY = "paramount_freeze_stand";

    public PlayerManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata(FREEZE_KEY) && event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.getPlayer().hasMetadata(FREEZE_KEY)) {
            if (event.getNewGameMode() == GameMode.SPECTATOR) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasMetadata(FREEZE_KEY)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(FREEZE_KEY)) {
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ() || event.getFrom().getY() != event.getTo().getY()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer().hasMetadata(FREEZE_KEY)) {
            PlayerUtil.unfreezeCamera(plugin, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStopSpectating(PlayerStopSpectatingEntityEvent event) {
        if (event.getPlayer().hasMetadata(FREEZE_KEY)) {
            event.setCancelled(true);
        }
    }
}
