package me.yamakaja.bukkitjs.script.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.function.Consumer;

/**
 * Created by Yamakaja on 06.05.17.
 * <p>
 * This is a "demo" class for ASM purposes
 */
public class JavaScriptEventWrapper implements Listener {

    private Consumer<Event> function;

    public JavaScriptEventWrapper(Consumer<Event> function) {
        this.function = function;
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent e) {
        function.accept(e);
    }

}
