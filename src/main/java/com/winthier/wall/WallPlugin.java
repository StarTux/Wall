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
        saveResource("walls.yml", false);
        reloadConfig();
        getCommand("wall").setExecutor(new WallCommand(this));
        getServer().getPluginManager().registerEvents(new WallEventListener(this), this);
        loadConfiguration();
        loadWalls();
    }

    void loadWalls() {
        walls.clear();
        commands.clear();
        YamlConfiguration config = YamlConfiguration
            .loadConfiguration(new File(getDataFolder(), "walls.yml"));
        for (String key: config.getKeys(false)) {
            Wall wall = new Wall(config.getConfigurationSection(key));
            walls.put(key, wall);
            if (wall.command != null) commands.put(wall.command, wall);
        }
    }

    void loadConfiguration() {
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
