package me.yamakaja.bukkitjs.script.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by Yamakaja on 07.05.17.
 */
public class CommandWrapper extends Command {

    private CommandConsumer consumer;

    public CommandWrapper(String name, String description, String usageMessage, List<String> aliases, CommandConsumer consumer) {
        super(name, description, usageMessage, aliases);
        this.consumer = consumer;
    }

    public CommandWrapper(String name, CommandConsumer consumer) {
        super(name);
        this.consumer = consumer;
    }

    @Override
    public boolean execute(CommandSender commandSender, String alias, String[] args) {
        consumer.accept(commandSender, alias, args);
        return true;
    }
}
