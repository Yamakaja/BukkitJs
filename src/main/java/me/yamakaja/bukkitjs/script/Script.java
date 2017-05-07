package me.yamakaja.bukkitjs.script;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
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
        this.name = file.getName().substring(0, file.getName().length() - 2);

        manager.getEngine().setContext(context);

        manager.getEngine().put("server", manager.getServerInterface());

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
