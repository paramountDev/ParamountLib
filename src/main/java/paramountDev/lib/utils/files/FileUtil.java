package paramountDev.lib.utils.files;

import org.bukkit.plugin.java.JavaPlugin;
import paramountDev.ParamountLib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


// Copyright 2026 ParamountDev Licensed under the Apache License, Version 2.0

public class FileUtil {

    public static void createFolder(JavaPlugin plugin, String name) {
        File folder = new File(plugin.getDataFolder(), name);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static boolean folderExists(String name) {
        File folder = new File(ParamountLib.getInstance().getDataFolder(), name);
        return folder.exists() && folder.isDirectory();
    }

    public static List<File> getFiles(String folderName, String extension) {
        File folder = new File(ParamountLib.getInstance().getDataFolder(), folderName);
        List<File> result = new ArrayList<>();

        if (!folder.exists() || !folder.isDirectory()) {
            return result;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(extension.toLowerCase())) {
                    result.add(file);
                }
            }
        }
        return result;
    }

    public static boolean deleteFolder(String folderName) {
        File folder = new File(ParamountLib.getInstance().getDataFolder(), folderName);
        return deleteRecursive(folder);
    }

    private static boolean deleteRecursive(File path) {
        if (!path.exists()) return false;

        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteRecursive(file);
                }
            }
        }
        return path.delete();
    }

    public static void clearFolder(String folderName) {
        File folder = new File(ParamountLib.getInstance().getDataFolder(), folderName);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteRecursive(file);
                }
            }
        }
    }

    public static boolean renameFolder(String oldName, String newName) {
        File oldFolder = new File(ParamountLib.getInstance().getDataFolder(), oldName);
        File newFolder = new File(ParamountLib.getInstance().getDataFolder(), newName);

        if (oldFolder.exists() && !newFolder.exists()) {
            return oldFolder.renameTo(newFolder);
        }
        return false;
    }

    public static void copyFolder(File source, File destination) {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String[] files = source.list();
            if (files != null) {
                for (String file : files) {
                    File srcFile = new File(source, file);
                    File destFile = new File(destination, file);
                    copyFolder(srcFile, destFile);
                }
            }
        } else {
            try {
                Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                ParamountLib.getInstance().getLogger().severe("Ошибка при копировании файла: " + source.getName());
                e.printStackTrace();
            }
        }
    }
}
