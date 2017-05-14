with (bukkitImports) {

    server.registerListener("#player/PlayerJoinEvent", function (event) {
        event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getName());
    });

    server.registerListener("#player/PlayerQuitEvent", function (event) {
        event.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getName());
    });

}