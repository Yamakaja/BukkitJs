package me.yamakaja.bukkitjs;

import java.io.Reader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class JSEngineManager {

	private ScriptEngine engine;

	public JSEngineManager() {
		engine = new ScriptEngineManager().getEngineByName("javascript");
		engine.put("out", System.out);
		try {
			if (engine != null) {
				if (System.getProperty("java.version").contains("1.8"))
					engine.eval("load('nashorn:mozilla_compat.js')");
				engine.eval("importPackage(org.bukkit);");
				engine.eval("importPackage(java);");
				engine.eval("importPackage(Packages.net.minecraft.server);");
			}
		} catch (ScriptException e) {
			throw new RuntimeException("Oops!", e);
		}
	}

	public void eval(String script) throws ScriptException {
		engine.eval(script);
	}

	public void eval(Reader reader) throws ScriptException {
		engine.eval(reader);
	}

	public void addScript(Script script) {
		try {
			engine.eval((new StringBuilder("var ")).append(script.getName()).append(" = function(event) {\n")
					.append(script.getCode()).append("\n}").toString());
		} catch (ScriptException e) {
			throw new RuntimeException("Oops!", e);
		}
	}

	public void addDummyScript(Script script) {
		try {
			engine.eval((new StringBuilder("var ")).append(script.getName()).append(" = function(player) {\n")
					.append(script.getCode()).append("\n}").toString());
		} catch (ScriptException e) {
			throw new RuntimeException("Oops!", e);
		}
	}
	
	public void addRunnableScript(Script script){
		try {
			engine.eval((new StringBuilder("var ")).append(script.getName()).append(" = new Object {\nrun: function(){\n")
					.append(script.getCode()).append("\n}\n}").toString());
//			System.out.println("Added runnable script: " + script.getName());
		} catch (ScriptException e) {
			throw new RuntimeException("Oops!", e);
		}
	}

	public void execute(String name, Event event) {
		try {
			((Invocable) engine).invokeFunction(name, new Object[] { event });
		} catch (Exception e) {
			throw new RuntimeException(new StringBuilder("Failed to execute script: ").append(name).toString(), e);
		}
	}

	public void execute(String name, Player p) {
		try {
			((Invocable) engine).invokeFunction(name, new Object[] { p });
		} catch (Exception e) {
			throw new RuntimeException(new StringBuilder("Failed to execute script: ").append(name).toString(), e);
		}
	}
	
	public Runnable getRunnable(Script s){
//		System.out.println("Runnable requested of script " + s.getName());
		try {
			return (Runnable) ((Invocable) engine).getInterface(engine.get(s.getName()), Runnable.class);
		}catch (IllegalArgumentException e){
			throw new RuntimeException(new StringBuilder("Failed to get Runnable: ").append(s.getName()).toString(), e);
		}
	}

}
