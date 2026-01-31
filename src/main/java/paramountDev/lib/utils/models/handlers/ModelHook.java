package paramountDev.lib.utils.models.handlers;

import org.bukkit.Bukkit;
import paramountDev.lib.utils.messages.MessageUtil;

public class ModelHook {

    private static IModelHandler handler;
    private static boolean enabled = false;

    public static void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("BetterModel")) {
            try {
                handler = new BetterModelHandler();
                enabled = true;
                MessageUtil.sendMessageToConsole("[ParamountLib] BetterModel hooked successfully!");
            } catch (Throwable e) {
                MessageUtil.sendMessageToConsole("[ParamountLib] BetterModel found but failed to hook: " + e.getMessage());
                handler = new FallbackModelHandler();
                enabled = false;
            }
        } else {
            handler = new FallbackModelHandler();
            enabled = false;
        }
    }

    public static IModelHandler get() {
        if (handler == null) init();
        return handler;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
