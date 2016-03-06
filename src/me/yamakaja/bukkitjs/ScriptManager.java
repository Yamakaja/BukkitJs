package me.yamakaja.bukkitjs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.bukkit.event.HandlerList;

import me.yamakaja.bukkitjs.EventManager.EventType;

public class ScriptManager {

	BukkitJs plugin;
	ArrayList<Script> scripts = new ArrayList<>();

	private static final String baseFolder = "/scripts";

	public ScriptManager(BukkitJs plugin) {
		this.plugin = plugin;
		loadScripts();
	}

	public void loadScripts() {
		ArrayList<Script> result = new ArrayList<>();
		File f = new File(plugin.getDataFolder().getAbsolutePath() + ScriptManager.baseFolder);
		if(!f.exists()){
			f.mkdir();
		}
		String code;
		EventType event;
		boolean enabled = true;
		String name;
		long frequency = 20;
		boolean async = true;
		for (File listFile : f.listFiles()) {
			code = "";
			enabled = true;
			if (listFile.isFile()) {
				try {
					if (listFile.getName().endsWith(".disabled")) {
						name = listFile.getName().split("\\.")[0];
						enabled = false;
					} else
						name = listFile.getName();
					Scanner s = new Scanner(listFile);
					event = EventType.valueOf(s.nextLine());

					if (event == EventType.TIMER) {
						try {
							frequency = Long.parseLong(s.nextLine());
							async = Boolean.valueOf(s.nextLine());
						} catch (NumberFormatException ex) {
							frequency = 20;
						}
					}

					while (s.hasNextLine()) {
						code += s.nextLine() + "\n";
					}
					s.close();

					Script script = null;
					
					if(event == EventType.TIMER){
						script = new Script(name, code, event, enabled, frequency, async);
					}else{
						script = new Script(name, code, event, enabled);
						if(enabled && !event.isDummy())
							plugin.eventManager.registerEvent(event.clazz);
						
					}

					result.add(script);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		for (Script s : result) {
			try {
				if (!s.enabled)
					continue;
				if (s.getEventType().isDummy())
					plugin.engineManager.addDummyScript(s);
				else if(s.getEventType().isEvent())
					plugin.engineManager.addScript(s);
				else{
					plugin.engineManager.addRunnableScript(s);
					plugin.schedulerManager.newTimerTask(s);
				}
//				System.out.println(s.getName() + " is a " + s.getEventType().name() + " script!");
			} catch (RuntimeException e) {
				System.out.println("There is a problem with the script \"" + s.getName() + "\"!");
				System.out.println(e.getCause().getLocalizedMessage());
			}
		}
		this.scripts = result;
	}

	public Script getScriptByName(String name) {
		for (Script s : this.scripts)
			if (s.getName().equalsIgnoreCase(name))
				return s;
		return null;
	}

	public void reloadScripts() {
		this.scripts.clear();
		HandlerList.unregisterAll(plugin.eventManager);
		plugin.schedulerManager.cancelAllTasks();
		plugin.eventManager.registeredListeners.clear();
		this.loadScripts();
	}

	public void deleteScript(Script script) {
		File output = getScriptFile(script);
		output.delete();
		reloadScripts();
	}

	public void toggleScriptStatus(Script s) {
		File f = getScriptFile(s);
		if (f.exists())
			f.delete();
		s.setEnabled(!s.enabled);
		saveScript(s);
		reloadScripts();
	}

	public File getScriptFile(Script s) {
		return new File(plugin.getDataFolder().getAbsolutePath() + baseFolder + "/" + s.getName()
				+ (s.isEnabled() ? "" : ".disabled"));
	}

	public void saveScript(Script script) {
		File output = getScriptFile(script);
		try {
			PrintWriter pw = new PrintWriter(output);
			pw.println(script.getEventType().name());
			if(script.getEventType() == EventType.TIMER){
				pw.println(script.frequency);
				pw.println(script.async);
			}
			pw.print(script.getCode());

			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void saveScripts() {
		for (Script s : scripts) {
			saveScript(s);
		}
	}
}
