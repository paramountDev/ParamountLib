package paramountDev;

import org.bukkit.plugin.java.JavaPlugin;
import paramountDev.lib.messages.MessageUtil;

import static paramountDev.lib.messages.MessageUtil.sendMessageToAllPlayersWithPermission;
import static paramountDev.lib.messages.MessageUtil.sendSignatureToConsole;

public final class ParamountLib extends JavaPlugin {

    private static ParamountLib instance;

    @Override
    public void onEnable() {
        instance = this;

        MessageUtil.init(this, "ParamountLib");
        MessageUtil.checkForAuthor(this);

        sendMessageToAllPlayersWithPermission("op", "Библиотека Бога запущена. Приятной игры.");
        sendSignatureToConsole("enabled");
    }

    @Override
    public void onDisable() {
        sendSignatureToConsole("disabled");
    }


    public static ParamountLib getInstance() {
        return instance;
    }
}
