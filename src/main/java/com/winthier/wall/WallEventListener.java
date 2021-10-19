package com.winthier.wall;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

@RequiredArgsConstructor
class WallEventListener implements Listener {
    final WallPlugin plugin;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event.getStatus() != PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) return;
        final List<String> list = plugin.getJoinWalls();
        if (list == null || list.isEmpty()) return;
        String key = list.get(ThreadLocalRandom.current().nextInt(list.size()));
        final Wall wall = plugin.getWalls().get(key);
        if (wall == null) return;
        if (!wall.hasPermission(event.getPlayer())) return;
        wall.send(event.getPlayer());
    }

    @EventHandler
    void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage();
        if (cmd.startsWith("/")) cmd = cmd.substring(1);
        Wall wall = plugin.commands.get(cmd);
        if (wall == null) return;
        Player player = event.getPlayer();
        if (!wall.hasPermission(player)) return;
        event.setCancelled(true);
        plugin.getLogger().info(player.getName() + " issued wall command: " + cmd);
        wall.send(player);
    }
}
