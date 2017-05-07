package me.yamakaja.bukkitjs.script;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import me.yamakaja.bukkitjs.BukkitJs;

import javax.script.ScriptEngineManager;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Yamakaja on 19.12.16.
 */
public class ScriptManager {

    private BukkitJs plugin;

    private HashMap<String, Script> scripts = new HashMap<>();

    private ServerInterface serverInterface = new ServerInterface(this);

    private NashornScriptEngine engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");

    public ScriptManager(BukkitJs plugin) {
        this.plugin = plugin;
    }

    public void loadScripts() {
        File scriptDir = new File(plugin.getDataFolder(), "scripts");

        if (!scriptDir.exists()) {
            scriptDir.mkdirs();
            return;
        } else if (!scriptDir.isDirectory()) {
            throw new RuntimeException("plugins/BukkitJs/scripts/ is present but not a directory?!");
        }

        File[] scriptFiles = scriptDir.listFiles();

        if (scriptFiles == null) {
            throw new RuntimeException("Can't view script dirs contents!");
        }

        Arrays.stream(scriptFiles).filter(file -> file.getName().endsWith(".js")).forEach(file -> scripts.put(file.getName(), new Script(file, this)));
    }

    public void unloadAll() {

    }

    public BukkitJs getPlugin() {
        return plugin;
    }

    public NashornScriptEngine getEngine() {
        return engine;
    }

    public HashMap<String, Script> getScripts() {
        return scripts;
    }

    public ServerInterface getServerInterface() {
        return serverInterface;
    }
}
