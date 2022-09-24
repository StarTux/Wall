package com.winthier.wall;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class WallPlugin extends JavaPlugin {
    final Map<String, Wall> walls = new HashMap<>();
    final Map<String, Wall> commands = new HashMap<>();
    List<String> joinWalls = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!new File(getDataFolder(), "walls.yml").exists()) {
            saveResource("walls.yml", false);
        }
        reloadConfig();
        getCommand("wall").setExecutor(new WallCommand(this));
        getServer().getPluginManager().registerEvents(new WallEventListener(this), this);
        loadConfiguration();
        loadWalls();
    }

    protected void loadWalls() {
        walls.clear();
        commands.clear();
        loadWalls(new File(getDataFolder(), "walls.yml"));
        loadWallFolder(new File(getDataFolder(), "walls"));
        loadWallFolder(new File("/home/mc/public/config/Wall/walls"));
    }

    protected void loadWallFolder(File folder) {
        if (!folder.isDirectory()) return;
        for (File file : folder.listFiles()) {
            loadWalls(file);
        }
    }

    protected void loadWalls(File file) {
        if (!file.isFile()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int wallCount = 0;
        for (String key: config.getKeys(false)) {
            Wall wall = new Wall(config.getConfigurationSection(key));
            walls.put(key, wall);
            wallCount += 1;
            if (wall.getCommand() != null) {
                commands.put(wall.getCommand(), wall);
            }
        }
        getLogger().info("Loaded " + wallCount + " walls from file " + file);
    }

    protected void loadConfiguration() {
        reloadConfig();
        final ConfigurationSection config = getConfig();
        joinWalls = config.getStringList("JoinWalls");
        if (joinWalls == null) {
            String joinWall = config.getString("JoinWall");
            joinWalls = joinWall != null
                ? List.of(joinWall)
                : List.of();
        }
        getLogger().info("Join Walls: " + joinWalls);
    }
}
