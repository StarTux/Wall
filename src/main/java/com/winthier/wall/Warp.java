package com.winthier.wall;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

@Data
public class Warp {
    String world, permission = null;
    double x, y ,z;
    float yaw, pitch;

    static Warp of(Location location) {
        Warp result = new Warp();
        result.world = location.getWorld().getName();
        result.x = location.getX();
        result.y = location.getY();
        result.z = location.getZ();
        result.pitch = location.getPitch();
        result.yaw = location.getYaw();
        return result;
    }

    Location getLocation() {
        World world = Bukkit.getServer().getWorld(this.world);
        if (world == null) return null;
        return new Location(world, x, y, z, yaw, pitch);
    }

    void serialize(ConfigurationSection config) {
        config.set("world", world);
        config.set("x", x);
        config.set("y", y);
        config.set("z", z);
        config.set("pitch", pitch);
        config.set("yaw", yaw);
        if (permission != null) config.set("permission", permission);
    }

    static Warp deserialize(ConfigurationSection config) {
        Warp result = new Warp();
        result.world = config.getString("world");
        result.x = config.getDouble("x");
        result.y = config.getDouble("y");
        result.z = config.getDouble("z");
        result.pitch = (float)config.getDouble("pitch");
        result.yaw = (float)config.getDouble("yaw");
        result.permission = config.getString("permission", null);
        return result;
    }
}
