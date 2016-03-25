package com.winthier.wall;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class HelpCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        final Player player = sender instanceof Player ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player expected");
        } else if (args.length == 0) {
            WallPlugin.getInstance().getWalls().get("help").send(player);
        } else {
            StringBuilder sb = new StringBuilder("help");
            for (String arg: args) {
                sb.append("-").append(arg.toLowerCase());
            }
            final Wall wall = WallPlugin.getInstance().getWalls().get(sb.toString());
            if (wall == null) {
                sender.sendMessage(ChatColor.RED + "No help for " + sb.toString());
            } else if (!wall.hasPermission(player)) {
                sender.sendMessage(ChatColor.RED + "No permission");
            } else {
                wall.send(player);
            }
        }
        return true;
    }
}
