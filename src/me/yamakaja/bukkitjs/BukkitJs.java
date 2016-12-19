package me.yamakaja.bukkitjs;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitJs extends JavaPlugin {

	boolean debug = false;

	Logger logger;

	@Override
	public void onEnable() {
		logger.info("Enabled!");
		logger.info("by Yamakaja");
	}

	@Override
	public void onDisable() {
		logger.info("Disabled!");
	}

}
