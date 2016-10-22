package com.winthier.wall;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class WallPlugin extends JavaPlugin
{
    @Getter static WallPlugin instance;
    final Map<String, Wall> walls = new HashMap<>();
    final Map<String, String> worldWalls = new HashMap<>();
    String joinWall = null;
    Map<String, Warp> warps = null;
    Map<String, String> aliases = null;
    
    @Override
    public void onEnable()
    {
        instance = this;
        getCommand("wall").setExecutor(new WallCommand());
        getCommand("help").setExecutor(new HelpCommand());
        getServer().getPluginManager().registerEvents(new WallEventListener(), this);
        saveResource("walls.yml", false);
        saveDefaultConfig();
        loadConfiguration();
        loadWalls();
    }

    void loadWalls() {
        walls.clear();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "walls.yml"));
        for (String key: config.getKeys(false)) {
            Wall wall = new Wall(config.getConfigurationSection(key));
            walls.put(key, wall);
        }
    }

    void loadConfiguration() {
        reloadConfig();
        final ConfigurationSection config = getConfig();
        joinWall = config.getString("JoinWall");
        worldWalls.clear();
        ConfigurationSection section = config.getConfigurationSection("WorldWalls");
        for (String key: section.getKeys(false)) {
            worldWalls.put(key, section.getString(key));
        }
    }

    Map<String, Warp> getWarps() {
        if (warps == null) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "warps.yml"));
            Map<String, Warp> warps = new HashMap<>();
            for (String key: config.getKeys(false)) {
                Warp warp = Warp.deserialize(config.getConfigurationSection(key));
                warps.put(key, warp);
            }
            this.warps = warps;
        }
        return warps;
    }

    void saveWarps() {
        if (walls == null) return;
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, Warp> entry: warps.entrySet()) {
            ConfigurationSection section = config.createSection(entry.getKey());
            entry.getValue().serialize(section);
        }
        try {
            config.save(new File(getDataFolder(), "warps.yml"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
