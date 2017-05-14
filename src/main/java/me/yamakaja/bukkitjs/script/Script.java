package me.yamakaja.bukkitjs.script;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Yamakaja on 19.12.16.
 */
public class Script {

    private File file;
    private String name;

    private ScriptContext context = new SimpleScriptContext();

    public Script(File file, ScriptManager manager) {
        this.file = file;
        this.name = file.getName().substring(0, file.getName().length() - 3);

        manager.getEngine().setContext(context);

        manager.getEngine().put("serverInterface", manager.getServerInterface());
        try {

            manager.getEngine().eval("var server = serverInterface;");
            manager.getEngine().eval("var bukkitImports = new JavaImporter(" +
                    "org.bukkit," +
                    "org.bukkit.block," +
                    "org.bukkit.block.banner," +
                    "org.bukkit.boss," +
                    "org.bukkit.command," +
                    "org.bukkit.configuration," +
                    "org.bukkit.enchantments," +
                    "org.bukkit.entity," +
                    "org.bukkit.entity.minecart," +
                    "org.bukkit.event," +
                    "org.bukkit.event.block," +
                    "org.bukkit.event.enchantment," +
                    "org.bukkit.event.entity," +
                    "org.bukkit.event.hanging," +
                    "org.bukkit.event.inventory," +
                    "org.bukkit.event.painting," +
                    "org.bukkit.event.player," +
                    "org.bukkit.event.server," +
                    "org.bukkit.event.vehicle," +
                    "org.bukkit.event.weather," +
                    "org.bukkit.event.world," +
                    "org.bukkit.inventory," +
                    "org.bukkit.inventory.meta," +
                    "org.bukkit.map," +
                    "org.bukkit.material," +
                    "org.bukkit.material.types," +
                    "org.bukkit.metadata," +
                    "org.bukkit.permissions," +
                    "org.bukkit.plugin," +
                    "org.bukkit.plugin.messaging," +
                    "org.bukkit.potion," +
                    "org.bukkit.projectiles," +
                    "org.bukkit.scheduler," +
                    "org.bukkit.scoreboard," +
                    "org.bukkit.util," +
                    "org.bukkit.util.io" +
                    ");");

            manager.getEngine().eval("var javaUtilImports = new JavaImporter(" +
                    "java.lang," +
                    "java.util," +
                    "java.text" +
                    ");");

            manager.getEngine().eval("var javaIOImports = new JavaImporter(" +
                    "java.io," +
                    "java.nio," +
                    "java.nio.channels," +
                    "java.nio.charset," +
                    "java.net" +
                    ");");

        } catch (ScriptException e) {
            e.printStackTrace();
        }

        try {
            manager.getEngine().eval(new FileReader(file));
        } catch (ScriptException | FileNotFoundException e) {
            System.out.println("Error while parsing script \"" + file.getName() + "\":");
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public ScriptContext getContext() {
        return context;
    }

}
