package me.yamakaja.bukkitjs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitTask;

public class SchedulerManager {

	public BukkitJs plugin;

	Map<String, Integer> tasks = new HashMap<>();

	public SchedulerManager(BukkitJs plugin) {
		this.plugin = plugin;
	}

	public void newTimerTask(Script s) {
//		System.out.println("newTimerTask invoked!");
		try{
		BukkitTask task;
		if (s.async)
			task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, plugin.engineManager.getRunnable(s), 0, s.frequency);
		else
			task = plugin.getServer().getScheduler().runTaskTimer(plugin, plugin.engineManager.getRunnable(s), 0, s.frequency);
		tasks.put(s.getName(), task.getTaskId());
		}catch(Exception e){
			System.out.println(e.getCause().getLocalizedMessage());
		}
	}

	public void cancelAllTasks() {
		plugin.getServer().getScheduler().cancelTasks(plugin);
		tasks.clear();
	}
	
	public void cancelTask(Script s){
		if(tasks.containsKey(s.getName())){
			plugin.getServer().getScheduler().cancelTask(tasks.get(s.getName()));
		}else{
			throw new RuntimeException("Task \"" + s.getName() + "\" not registered!");
		}
	}

}
