package com.winthier.wall;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class WallCommand implements CommandExecutor {
    final WallPlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;
        final Player player = sender instanceof Player ? (Player) sender : null;
        String firstArg = args[0];
        if (firstArg.startsWith("-")) {
            if (!sender.hasPermission("wall.admin")) {
                sender.sendMessage(ChatColor.RED + "No permission");
                return true;
            }
            firstArg = firstArg.substring(1);
            if (firstArg.equals("reload")) {
                plugin.loadConfiguration();
                plugin.loadWalls();
                sender.sendMessage("Walls reloaded");
            }
        } else if (args.length == 1) {
            Wall wall = plugin.getWalls().get(firstArg);
            if (wall == null) {
                sender.sendMessage(ChatColor.RED + "Not found: " + firstArg);
            } else if (!wall.hasPermission(sender)) {
                sender.sendMessage(ChatColor.RED + "No permission");
            } else {
                wall.send(sender);
            }
        } else {
            return false;
        }
        return true;
    }
}
