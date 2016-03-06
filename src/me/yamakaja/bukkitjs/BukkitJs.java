package me.yamakaja.bukkitjs;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitJs extends JavaPlugin {

	boolean debug = false;

	Logger logger;
	ScriptManager scriptManager;
	JSEngineManager engineManager;
	EventManager eventManager;
	SchedulerManager schedulerManager;

	ScriptCommand scriptCommand;

	@Override
	public void onEnable() {
		
		long starttime = System.currentTimeMillis();
		this.logger = this.getLogger();
		if (debug) {
			logger.info("[Timings] Logger: " + (System.currentTimeMillis() - starttime) + "ms");
			starttime = System.currentTimeMillis();
		}

		this.schedulerManager = new SchedulerManager(this);
		if (debug) {
			logger.info("[Timings] Scheduler: " + (System.currentTimeMillis() - starttime) + "ms");
			starttime = System.currentTimeMillis();
		}
		this.eventManager = new EventManager(this);
		if (debug) {
			logger.info("[Timings] EventManager: " + (System.currentTimeMillis() - starttime) + "ms");
			starttime = System.currentTimeMillis();
		}
		this.engineManager = new JSEngineManager();
		if (debug) {
			logger.info("[Timings] JavaScript engine: " + (System.currentTimeMillis() - starttime) + "ms");
			starttime = System.currentTimeMillis();
		}
		this.scriptManager = new ScriptManager(this);
		if (debug)
			logger.info("[Timings] ScriptLoader: " + (System.currentTimeMillis() - starttime) + "ms");

		logger.info("Enabled!");
		logger.info("by Yamakaja");

		init();
	}

	@Override
	public void onDisable() {
		logger.info("Disabled!");
		schedulerManager.cancelAllTasks();
	}

	private void init() {
		scriptCommand = new ScriptCommand(this);
		this.getCommand("script").setExecutor(scriptCommand);
		this.getCommand("script").setTabCompleter(scriptCommand);
	}

}
