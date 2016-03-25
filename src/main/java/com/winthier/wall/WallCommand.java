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
            firstArg = firstArg.substring(1);
            if (firstArg.equals("reload")) {
                if (!sender.hasPermission("wall.reload")) {
                    sender.sendMessage(ChatColor.RED + "No permission");
                } else {
                    WallPlugin.getInstance().loadConfiguration();
                    WallPlugin.getInstance().loadWalls();
                    sender.sendMessage("Walls reloaded");
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
