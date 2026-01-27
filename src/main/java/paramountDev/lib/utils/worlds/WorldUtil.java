package paramountDev.lib.utils.worlds;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class WorldUtil {

    public static World load(String worldName) {
        return new WorldCreator(worldName).createWorld();
    }

    public static void unload(String worldName, boolean save) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.unloadWorld(world, save);
        }
    }

    public static void delete(String worldName) {
        unload(worldName, false);
        File path = new File(Bukkit.getWorldContainer(), worldName);
        deleteFile(path);
    }

    public static void copy(String sourceName, String targetName) {
        unload(sourceName, true);
        unload(targetName, false);

        File source = new File(Bukkit.getWorldContainer(), sourceName);
        File target = new File(Bukkit.getWorldContainer(), targetName);

        try {
            copyFolder(source, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clean(World world) {
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player)) {
                entity.remove();
            }
        }
    }

    public static void cleanItems(World world) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof org.bukkit.entity.Item) {
                entity.remove();
            }
        }
    }

    public static void setupArenaRules(World world) {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);

        world.setTime(6000);
        world.setStorm(false);
        world.setThundering(false);
        world.setDifficulty(Difficulty.NORMAL);
    }

    private static void deleteFile(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFile(file);
                    } else {
                        file.delete();
                    }
                }
            }
            path.delete();
        }
    }

    private static void copyFolder(File source, File target) throws IOException {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdirs();
            }
            String[] files = source.list();
            if (files != null) {
                for (String file : files) {
                    if (file.equals("uid.dat") || file.equals("session.lock")) continue;
                    File srcFile = new File(source, file);
                    File destFile = new File(target, file);
                    copyFolder(srcFile, destFile);
                }
            }
        } else {
            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new FileOutputStream(target)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        }
    }
}
