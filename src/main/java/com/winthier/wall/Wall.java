package com.winthier.wall;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.separator;

@Getter
public final class Wall {
    private final String name;
    private final String permission;
    private final String command;
    private final boolean book;
    private final List<Component> lines = new ArrayList<>();

    public Wall(final ConfigurationSection config) {
        this.name = config.getName();
        this.permission = config.getString("Permission", null);
        for (Object o: config.getList("lines")) {
            final Line line = Line.of(o);
            if (line != null) {
                lines.add(line.toComponent());
            }
        }
        this.command = config.getString("Command");
        this.book = config.getBoolean("Book");
    }

    public void send(CommandSender sender) {
        Component component = join(separator(newline()), lines);
        if (book && sender instanceof Player player) {
            ItemStack bookItem = new ItemStack(Material.WRITTEN_BOOK);
            bookItem.editMeta(m -> {
                    if (m instanceof BookMeta meta) {
                        meta.pages(List.of(component));
                        meta.author(text("Cavetale"));
                        meta.title(text("Wall"));
                    }
                });
            player.openBook(bookItem);
            return;
        }
        sender.sendMessage(component);
    }

    public boolean hasPermission(CommandSender sender) {
        return permission == null || permission.isEmpty()
            || sender.hasPermission(permission);
    }
}
