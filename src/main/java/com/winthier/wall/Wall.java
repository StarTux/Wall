package com.winthier.wall;

import com.cavetale.core.font.Emoji;
import com.cavetale.core.font.GlyphPolicy;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

@Getter
class Wall {
    final String name;
    final String permission;
    final String command;
    final Component component;

    Wall(final ConfigurationSection config) {
        this.name = config.getName();
        this.permission = config.getString("Permission", null);
        final List<Component> lines = new ArrayList<>();
        for (Object o: config.getList("lines")) {
            final Line line = Line.of(o);
            if (line != null) {
                lines.add(line.toComponent());
            }
        }
        component = Component.join(Component.newline(), lines);
        command = config.getString("Command");
    }

    void send(CommandSender sender) {
        sender.sendMessage(Emoji.replaceText(component, GlyphPolicy.HIDDEN));
    }

    boolean hasPermission(CommandSender sender) {
        return permission == null || permission.isEmpty()
            || sender.hasPermission(permission);
    }
}
