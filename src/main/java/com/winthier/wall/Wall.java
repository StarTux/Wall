package com.winthier.wall;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@Getter
class Wall {
    final String name;
    final String permission;
    final List<Line> lines = new ArrayList<>();
    final String command;

    Wall(final ConfigurationSection config) {
        this.name = config.getName();
        this.permission = config.getString("Permission", null);
        for (Object o: config.getList("lines")) {
            final Line line = Line.of(o);
            if (line != null) {
                lines.add(line);
            }
        }
        command = config.getString("Command");
    }

    void send(Player player) {
        for (Line line: lines) {
            line.send(player);
        }
    }

    boolean hasPermission(CommandSender sender) {
        return permission == null || permission.isEmpty()
            || sender.hasPermission(permission);
    }
}
