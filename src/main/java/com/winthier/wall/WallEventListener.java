package com.winthier.wall;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

class WallEventListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onPlayerJoin(PlayerJoinEvent event) {
        final String key = WallPlugin.getInstance().getJoinWall();
        if (key == null) return;
        final Wall wall = WallPlugin.getInstance().getWalls().get(key);
        if (wall == null) return;
        if (!wall.hasPermission(event.getPlayer())) return;
        wall.send(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        final String key = WallPlugin.getInstance().getWorldWalls().get(event.getPlayer().getWorld().getName());
        if (key == null) return;
        final Wall wall = WallPlugin.getInstance().getWalls().get(key);
        if (wall == null) return;
        if (!wall.hasPermission(event.getPlayer())) return;
        wall.send(event.getPlayer());
    }

    @EventHandler
    void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().toLowerCase();
        if (cmd.startsWith("/")) cmd = cmd.substring(1);
        Location loc = WallPlugin.getInstance().getWarps().get(cmd);
        if (loc == null) return;
        event.setCancelled(true);
        event.getPlayer().teleport(loc);
        WallPlugin.getInstance().getLogger().info("Sent " + event.getPlayer().getName() + " to warp " + cmd);
    }
}
