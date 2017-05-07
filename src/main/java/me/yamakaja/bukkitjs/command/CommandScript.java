package me.yamakaja.bukkitjs.command;

import me.yamakaja.bukkitjs.BukkitJs;
import me.yamakaja.bukkitjs.script.Script;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

/**
 * Created by Yamakaja on 07.05.17.
 */
public class CommandScript implements CommandExecutor, TabCompleter {

    private BukkitJs plugin;

    public CommandScript(BukkitJs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("bukkitjs.admin")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED.toString() + "Type " + ChatColor.ITALIC + "/" + command.getName() + " help" + ChatColor.RED + " for a list of sub-commands!");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                sender.sendMessage("Enabled scripts:");
                for (Script script : plugin.getManager().getScripts().values())
                    sender.sendMessage(" - " + ChatColor.GREEN + script.getName());
                break;
            case "help":
                sender.sendMessage("Available sub-commands:");
                sender.sendMessage(" - " + ChatColor.GOLD + "help" + ChatColor.RESET + ": Shows this help");
                sender.sendMessage(" - " + ChatColor.GOLD + "list" + ChatColor.RESET + ": List scripts");
                break;

        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

}
