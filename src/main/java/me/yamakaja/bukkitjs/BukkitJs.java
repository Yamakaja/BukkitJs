package me.yamakaja.bukkitjs;

import me.yamakaja.bukkitjs.command.CommandScript;
import me.yamakaja.bukkitjs.script.ScriptManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitJs extends JavaPlugin {

    private ScriptManager manager;

    @Override
    public void onEnable() {
        manager = new ScriptManager(this);
        manager.loadScripts();

        CommandScript commandScript = new CommandScript(this);
        PluginCommand script = getCommand("script");
        script.setExecutor(commandScript);
        script.setTabCompleter(commandScript);

    }

    @Override
    public void onDisable() {
        manager.unloadAll();
    }

    public ScriptManager getManager() {
        return manager;
    }

}
