package paramountDev.lib.utils.messages;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class MessageUtil {

    private static JavaPlugin plugin;
    private static String PLUGIN_NAME = "PLUGIN";
    private static ChatColor MAIN_COLOR = ChatColor.LIGHT_PURPLE;
    private static ChatColor SECONDARY_COLOR = ChatColor.DARK_PURPLE;
    private static final Map<UUID, BukkitTask> actionBarTasks = new HashMap<>();
    private static final String[] suffix = new String[]{"", "k", "M", "B", "T"};

    public static void init(JavaPlugin pl, String name) {
        plugin = pl;
        PLUGIN_NAME = name;
    }

    public static void init(JavaPlugin pl, String name, ChatColor mainColor, ChatColor secondaryColor) {
        plugin = pl;
        PLUGIN_NAME = name;
        MAIN_COLOR = mainColor;
        SECONDARY_COLOR = secondaryColor;
    }


    public static TextComponent getPrefixComponent() {
        TextComponent bracketLeft = new TextComponent("[ ");
        bracketLeft.setColor(SECONDARY_COLOR);
        bracketLeft.setBold(true);

        TextComponent mainText = new TextComponent(PLUGIN_NAME);
        mainText.setColor(MAIN_COLOR);
        mainText.setBold(true);

        TextComponent bracketRight = new TextComponent(" ]");
        bracketRight.setColor(SECONDARY_COLOR);
        bracketRight.setBold(true);

        TextComponent prefixEnd = new TextComponent(" ➣");
        prefixEnd.setColor(ChatColor.WHITE);
        prefixEnd.setBold(true);


        ComponentBuilder hoverText = new ComponentBuilder("");
        hoverText.append("◄ ").color(ChatColor.DARK_PURPLE).bold(true);
        hoverText.append("Made by ParamountDev").color(ChatColor.LIGHT_PURPLE).bold(true);
        hoverText.append(" ►").color(ChatColor.DARK_PURPLE).bold(true);

        TextComponent fullPrefix = new TextComponent("");

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.create());
        bracketLeft.setHoverEvent(hoverEvent);
        mainText.setHoverEvent(hoverEvent);
        bracketRight.setHoverEvent(hoverEvent);
        prefixEnd.setHoverEvent(hoverEvent);

        fullPrefix.addExtra(bracketLeft);
        fullPrefix.addExtra(mainText);
        fullPrefix.addExtra(bracketRight);
        fullPrefix.addExtra(prefixEnd);

        return fullPrefix;
    }
    public static void sendMessageWithPrefix(Player player, String message) {
        message = color(message);

        TextComponent finalMessage = new TextComponent("");

        finalMessage.addExtra(getPrefixComponent());
        for (BaseComponent component : TextComponent.fromLegacyText(" " + message)) {
            finalMessage.addExtra(component);
        }

        player.spigot().sendMessage(finalMessage);
    }
    public static void sendAuthorMessage(Player player) {
        String separator = "§5§m+------------------------------------------+";

        player.sendMessage(separator);
        player.sendMessage("");
        player.sendMessage("   " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "⚡ " + ChatColor.LIGHT_PURPLE + PLUGIN_NAME + ChatColor.DARK_PURPLE + ChatColor.BOLD + " ⚡");
        player.sendMessage("");
        player.sendMessage("   " + ChatColor.LIGHT_PURPLE + "Автор: " + ChatColor.DARK_PURPLE + "ParamountDev");
        player.sendMessage("");
        player.sendMessage("   " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "[ ССЫЛКИ ]");

        TextComponent funpayLine = new TextComponent("   §5» ");
        TextComponent funpayLink = new TextComponent("§dFunPay Профиль");
        funpayLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://funpay.com/uk/users/14397429/"));
        funpayLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§dНажмите, чтобы открыть профиль FunPay").create()));
        funpayLine.addExtra(funpayLink);
        player.spigot().sendMessage(funpayLine);

        TextComponent tgLine = new TextComponent("   §5» ");
        TextComponent tgLink = new TextComponent("§dTelegram: @paramount1_dev");
        tgLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://t.me/paramount1_dev"));
        tgLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§dНажмите, чтобы открыть Telegram").create()));
        tgLine.addExtra(tgLink);
        player.spigot().sendMessage(tgLine);

        player.sendMessage("");
        player.sendMessage(separator);
    }
    public static String color(String text) {
        if (text == null) return "";
        Pattern hexPattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = hexPattern.matcher(text);
        while (matcher.find()) {
            String hexCode = text.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace("&#", "x");
            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) builder.append("&").append(c);
            text = text.replace(hexCode, builder.toString());
        }
        Pattern bracketPattern = Pattern.compile("<#[a-fA-F0-9]{6}>");
        Matcher bracketMatcher = bracketPattern.matcher(text);
        while (bracketMatcher.find()) {
            String hexCode = text.substring(bracketMatcher.start(), bracketMatcher.end());
            String colorCode = hexCode.substring(1, 8);
            text = text.replace(hexCode, ChatColor.of(colorCode).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    public static String formatTime(int totalSeconds) {
        if (totalSeconds < 60) return totalSeconds + " сек.";
        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("д. ");
        if (hours > 0) sb.append(hours).append("ч. ");
        if (minutes > 0) sb.append(minutes).append("м. ");
        if (seconds > 0) sb.append(seconds).append("с.");

        return sb.toString().trim();
    }
    public static String formatNumber(double number) {
        int r = 0;
        while (number >= 1000 && r < suffix.length - 1) {
            number /= 1000;
            r++;
        }
        return String.format(Locale.US, "%.1f%s", number, suffix[r]).replace(".0", "");
    }
    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) return;
        final String coloredMessage = color(message);

        if (actionBarTasks.containsKey(player.getUniqueId())) {
            actionBarTasks.get(player.getUniqueId()).cancel();
            actionBarTasks.remove(player.getUniqueId());
        }

        org.bukkit.scheduler.BukkitTask task = new org.bukkit.scheduler.BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (!player.isOnline() || count >= 4) {
                    actionBarTasks.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(coloredMessage));

                count++;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        actionBarTasks.put(player.getUniqueId(), task);
    }
    public static void sendActionBarToAll(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendActionBar(player, message);
        }
    }
    public static void checkForAuthor(Plugin plugin) {
        plugin.saveDefaultConfig();

        String author = plugin.getConfig().getString("author");

        if (!author.equals("ParamountDev")) {
            sendAuthorErrorMessage();
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
    }
    public static void sendAuthorErrorMessage() {
        final String reset = "\u001B[0m";
        final int width = 58;
        final String borderColor = "\u001B[95m";

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        sendEmptyLinesToConsole(8, console);

        console.sendMessage(borderColor + "╔" + "═".repeat(width) + "╗" + reset);
        console.sendMessage(colorizeCenteredText("Do not change Author value in config.", width, "\u001B[35m", "\u001B[95m", "\u001B[35m") + reset);
        console.sendMessage(borderColor + "╚" + "═".repeat(width) + "╝" + reset);

        sendEmptyLinesToConsole(8, console);
    }
    public static void sendSignatureToConsole(String pluginStatus) {
        final String reset = "\u001B[0m";
        final int width = 58;
        final String borderColor = "\u001B[95m";

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        sendEmptyLinesToConsole(8, console);

        console.sendMessage(borderColor + "╔" + "═".repeat(width) + "╗" + reset);

        console.sendMessage(colorizeCenteredText("⚡ " + PLUGIN_NAME + " Plugin " + pluginStatus + " ⚡", width, "\u001B[35m", "\u001B[32m", "\u001B[35m") + reset);
        console.sendMessage(colorizeCenteredText("Made by ParamountDev", width, "\u001B[35m", "\u001B[95m", "\u001B[35m") + reset);
        sendEmptyLinesToConsole(1, console);
        console.sendMessage(colorizeCenteredText("✉ Author Telegram: https://t.me/paramount1_dev ✉", width, "\u001B[96m", "\u001B[97m", "\u001B[96m") + reset);
        console.sendMessage(colorizeCenteredText("✉ Author FunPay: https://funpay.com/uk/users/14397429/ ✉", width, "\u001B[96m", "\u001B[97m", "\u001B[96m") + reset);
        console.sendMessage(borderColor + "╚" + "═".repeat(width) + "╝" + reset);

        sendEmptyLinesToConsole(8, console);
    }
    public static void sendEmptyLinesToConsole(int count, ConsoleCommandSender console) {
        for (int i = 0; i < count; i++) {
            console.sendMessage("");
        }
    }
    public static String colorizeCenteredText(String text, int width, String colorStart, String colorMiddle, String colorEnd) {
        String cleanText = text.replaceAll("\u001B\\[[;\\d]*m", "");
        int textLength = cleanText.length();

        int totalPadding = width - textLength;
        int paddingLeft = totalPadding / 2;
        int paddingRight = totalPadding - paddingLeft;

        StringBuilder colored = new StringBuilder();
        colored.append(" ".repeat(Math.max(0, paddingLeft)));

        if (textLength == 0) {
            colored.append(" ".repeat(textLength));
        } else if (textLength == 1) {
            colored.append(colorStart).append(text);
        } else {
            String firstChar = text.substring(0, 1);
            String middleChars = text.substring(1, text.length() - 1);
            String lastChar = text.substring(text.length() - 1);

            colored.append(colorStart).append(firstChar);
            colored.append(colorMiddle != null ? colorMiddle : colorStart).append(middleChars);
            if (colorEnd != null) {
                colored.append(colorEnd).append(lastChar);
            } else {
                colored.append(colorStart).append(lastChar);
            }
        }

        colored.append(" ".repeat(Math.max(0, paddingRight)));
        return colored.toString();
    }
    public static void sendMessageToAllPlayers(String... message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
    public static void sendMessageToAllPlayersWithPermission(Permission permission, String message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
    }
    public static void sendMessageToAllPlayersWithPermission(String permission, String message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission(permission)) {
                sendMessageWithPrefix(player, message);
            }
        }
    }
    public static void sendMessageToConsole(String message) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        console.sendMessage(message);
    }

}
