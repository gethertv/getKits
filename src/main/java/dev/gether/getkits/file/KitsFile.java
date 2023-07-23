package dev.gether.getkits.file;

import dev.gether.getkits.GetKits;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class KitsFile {

    private static File file;
    private static FileConfiguration config;

    public static void setup() {
        file = new File(GetKits.getInstance().getDataFolder(), "kits.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            GetKits.getInstance().saveResource("kits.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Nie mozna zapisac pliku!");
        }
    }


    public static void loadFile() {
        setup();
        save();
    }

}
