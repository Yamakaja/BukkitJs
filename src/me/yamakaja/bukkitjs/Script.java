package me.yamakaja.bukkitjs;

import me.yamakaja.bukkitjs.EventManager.EventType;

public class Script {
	
	private String code;
	private String name;
	private EventType event;
	public boolean enabled = true;
	
	public long frequency = 20;
	public boolean async = true;
	
	public Script(String name, String code, EventType event){
		this.name = name;
		this.code = code;
		this.event = event;
	}
	
	public Script(String name, String code, EventType event, boolean enabled){
		this.name = name;
		this.code = code;
		this.event = event;
		this.enabled = enabled;
	}
	
	public Script(String name, String code, EventType event, boolean enabled, long frequency, boolean async){
		this.name = name;
		this.code = code;
		this.event = event;
		this.enabled = enabled;
		
		this.frequency = frequency;
		this.async = async;
	}
	
	public String getCode(){
		return code;
	}
	
	public String getName(){
		return name;
	}
	
	public EventType getEventType(){
		return event;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
}
