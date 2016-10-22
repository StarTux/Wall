package com.winthier.wall;

import org.bukkit.Location;
import org.bukkit.entity.Player;
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
        Warp warp = WallPlugin.getInstance().getWarps().get(cmd);
        if (warp == null) return;
        Player player = event.getPlayer();
        String perm = warp.getPermission();
        if (perm != null && !perm.isEmpty() && !player.hasPermission(perm)) return;
        event.setCancelled(true);
        Location loc = warp.getLocation();
        if (loc == null) {
            WallPlugin.getInstance().getLogger().warning("Warp '" + cmd + "' has invalid world: '" + warp.getWorld() + "'");
            return;
        }
        player.teleport(loc);
        WallPlugin.getInstance().getLogger().info("Sent " + player.getName() + " to warp " + cmd);
    }
}
