package paramountDev.lib.utils.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import paramountDev.ParamountLib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static paramountDev.lib.utils.messages.MessageUtil.color;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class ConfigUtil {

    public static FileConfiguration load(JavaPlugin plugin, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            try {
                plugin.saveResource(fileName, false);
            } catch (IllegalArgumentException e) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException ex) {
                    plugin.getLogger().severe("Не удалось создать файл " + fileName);
                }
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void save(JavaPlugin plugin, FileConfiguration config, String fileName) {
        try {
            config.save(new File(plugin.getDataFolder(), fileName));
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить файл " + fileName);
            e.printStackTrace();
        }
    }

    public static String getColoredString(FileConfiguration config, String path) {
        if (!config.contains(path)) return null;
        return color(config.getString(path));
    }

    public static List<String> getColoredList(FileConfiguration config, String path) {
        if (!config.contains(path)) return new ArrayList<>();
        List<String> original = config.getStringList(path);
        List<String> colored = new ArrayList<>();
        for (String line : original) {
            colored.add(color(line));
        }
        return colored;
    }
}
