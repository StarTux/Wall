package com.winthier.wall;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
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
}
