package me.yamakaja.bukkitjs.script.command;

import org.bukkit.command.CommandSender;

/**
 * Created by Yamakaja on 07.05.17.
 */
@FunctionalInterface
public interface CommandConsumer {

    void accept(CommandSender sender, String alias, String[] args);

}
