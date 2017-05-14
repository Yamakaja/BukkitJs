with (bukkitImports) {

    server.registerCommand("fly", "Toggle survival flight", "/fly", ["f"], function (sender, alias, args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + "You can't use this command!");
            return;
        }

        sender.setAllowFlight(!sender.getAllowFlight());
        sender.sendMessage(ChatColor.GOLD + "Flight mode: " + (sender.getAllowFlight() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"))
    })

}