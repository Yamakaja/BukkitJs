package me.yamakaja.bukkitjs.script;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import me.yamakaja.bukkitjs.script.command.CommandConsumer;
import me.yamakaja.bukkitjs.script.command.CommandWrapper;
import me.yamakaja.bukkitjs.script.event.EventListenerGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by Yamakaja on 07.05.17.
 */
@SuppressWarnings("unused") // Methods are used from JavaScript code
public class ServerInterface {

    private ScriptManager scriptManager;
    private CommandMap commandMap;

    private EventListenerGenerator generator = new EventListenerGenerator();

    public ServerInterface(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;

        Server server = Bukkit.getServer();
        try {
            Field commandMap = server.getClass().getDeclaredField("commandMap");
            commandMap.setAccessible(true);
            this.commandMap = (CommandMap) commandMap.get(server);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void command(String name, String description, String usage, ScriptObjectMirror aliases, CommandConsumer commandConsumer) {
        commandMap.register(name, new CommandWrapper(name, description, usage, Arrays.asList(aliases != null && !aliases.isEmpty() ? (String[]) ScriptUtils.convert(aliases, String[].class) : new String[0]), commandConsumer));
    }

    public Listener on(String event, Consumer<Event> function) {
        Listener listener = generator.makeListener(function, event);
        scriptManager.getPlugin().getServer().getPluginManager().registerEvents(listener, scriptManager.getPlugin());
        return listener;
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public BukkitTask delay(boolean async, long by, Runnable function) {
        if (!async)
            return scriptManager.getPlugin().getServer().getScheduler().runTaskLater(scriptManager.getPlugin(), function, by);
        return scriptManager.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(scriptManager.getPlugin(), function, by);
    }

    public BukkitTask repeat(boolean async, long startDelay, long period, Runnable function) {
        if (!async)
            return scriptManager.getPlugin().getServer().getScheduler().runTaskTimer(scriptManager.getPlugin(), function, startDelay, period);
        return scriptManager.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(scriptManager.getPlugin(), function, startDelay, period);
    }

}
