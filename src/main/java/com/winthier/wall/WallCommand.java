package com.winthier.wall;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class WallCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length == 0) return false;
        final Player player = sender instanceof Player ? (Player)sender : null;
        String firstArg = args[0];
        if (firstArg.startsWith("-")) {
            if (!sender.hasPermission("wall.admin")) {
                sender.sendMessage(ChatColor.RED + "No permission");
                return true;
            }
            firstArg = firstArg.substring(1);
            if (firstArg.equals("reload")) {
                WallPlugin.getInstance().loadConfiguration();
                WallPlugin.getInstance().loadWalls();
                WallPlugin.getInstance().warps = null;
                sender.sendMessage("Walls reloaded");
            } else if (firstArg.equals("setwarp") && args.length == 2) {
                if (player == null) {
                    sender.sendMessage("Player expected");
                } else {
                    String warpName = args[1].toLowerCase();
                    WallPlugin.getInstance().getWarps().put(warpName, Warp.of(player.getLocation()));
                    WallPlugin.getInstance().saveWarps();
                    sender.sendMessage("Warp created: " + warpName);
                }
            }
        } else if (args.length == 1) {
            Wall wall = WallPlugin.getInstance().getWalls().get(firstArg);
            if (wall == null) {
                sender.sendMessage(ChatColor.RED + "Not found: " + firstArg);
            } else if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player expected");
            } else if (!wall.hasPermission(sender)) {
                sender.sendMessage(ChatColor.RED + "No permission");
            } else {
                wall.send(player);
            }
        } else {
            return false;
        }
        return true;
    }
}
