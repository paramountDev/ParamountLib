package paramountDev;

import org.bukkit.plugin.java.JavaPlugin;
import paramountDev.lib.managers.entities.EntityManager;
import paramountDev.lib.managers.inventories.InventoryManager;
import paramountDev.lib.managers.items.ItemManager;
import paramountDev.lib.utils.messages.MessageUtil;

import static paramountDev.lib.utils.messages.MessageUtil.checkForAuthor;
import static paramountDev.lib.utils.messages.MessageUtil.sendMessageToAllPlayersWithPermission;
import static paramountDev.lib.utils.messages.MessageUtil.sendSignatureToConsole;

public final class ParamountLib extends JavaPlugin {

    private static ParamountLib instance;

    @Override
    public void onEnable() {
        instance = this;

        MessageUtil.init(this, "ParamountLib");
        checkForAuthor(this);

        setUpListeners();

        sendMessageToAllPlayersWithPermission("op", "Библиотека Бога запущена. Приятной игры.");
        sendSignatureToConsole("enabled");
    }

    @Override
    public void onDisable() {
        sendSignatureToConsole("disabled");
    }

    private void setUpListeners() {
        new TestEntityManager(this);

        new EntityManager(this);
        new InventoryManager(this);
        new ItemManager(this);
    }

    public static ParamountLib getInstance() {
        return instance;
    }
}
