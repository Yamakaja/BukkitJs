package me.yamakaja.bukkitjs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.yamakaja.bukkitjs.EventManager.EventType;
import mkremins.fanciful.FancyMessage;

public class IngameEditor {
	private String[] code;
	private int cursorLine = 0;
	private Player owner;
	private EditMode mode = EditMode.INSERT;
	private EventType et;
	private String name;
	private long frequency;
	private boolean async;
	private String lineStore = "";
	private boolean enabled;

	public IngameEditor(Player owner, String name, String code, EventType e, int frequency, boolean async, boolean enabled) {
		this.owner = owner;
		this.code = getArrayFromString(code);
		this.et = e;
		this.name = name;
		this.frequency = frequency;
		this.async = async;
		this.enabled = enabled;
		sendEditScreen();
	}

	public IngameEditor(Player owner, String name, String code, EventType e, boolean enabled) {
		this.owner = owner;
		this.code = getArrayFromString(code);
		this.et = e;
		this.name = name;
		this.enabled = enabled;
		sendEditScreen();
	}

	public IngameEditor(Player owner, Script script) {
		this.owner = owner;
		this.code = getArrayFromString(script.getCode());
		this.et = script.getEventType();
		this.name = script.getName();
		this.frequency = script.frequency;
		this.async = script.async;
		this.enabled = script.enabled;
		sendEditScreen();
	}

	private String[] getArrayFromString(String string) {
		return string.split("\n");
	}

	public String getCode() {
		String result = "";
		for (String line : code)
			result += line + "\n";
		return result;
	}

	public String getCode(int index) {
		return code[index];
	}

	public void processLine(String lineX) {
		// System.out.println("EditMode." + this.mode.name());
		String line = new String(lineX);
		if (line.startsWith("_")){
			line = line.replace('_', ' ');
//			System.out.println(line);
		}
		
		
		if(line.endsWith("#")){
			lineStore += line.substring(0, line.length()-1);;
			return;
		} else if(!lineStore.isEmpty()){
			line = lineStore + line;
		}

		if (this.mode == EditMode.INSERT) {
			insertLine(line);
		} else if (this.mode == EditMode.OVERWRITE) {
			updateLine(line);
		}
		this.setEditMode(EditMode.INSERT);
		sendEditScreen();
	}

	public void deleteLine(int line) {
		if (code.length == 1)
			code[0] = "";
		else {
			List<String> temp = new ArrayList<>();
			temp.addAll(Arrays.asList(Arrays.copyOfRange(this.code, 0, line)));
			temp.addAll(Arrays.asList(Arrays.copyOfRange(this.code, line + 1, code.length)));
			this.code = temp.toArray(new String[temp.size()]);
		}

		if (cursorLine >= line)
			cursorLine--;
		sendEditScreen();
	}

	public void insertLine(String line) {
		// System.out.println("cursor: " + cursorLine + ", code.length: " +
		// code.length);
		if (this.cursorLine < this.code.length - 1) {
			List<String> temp = new ArrayList<>();
			temp.addAll(Arrays.asList(Arrays.copyOfRange(this.code, 0, this.cursorLine + 1)));
			temp.add(line);
			temp.addAll(Arrays.asList(Arrays.copyOfRange(this.code, this.cursorLine + 1, this.code.length)));
			this.code = temp.toArray(this.code);
		} else if (this.code.length == 0) {
			this.code = new String[] { line };
		} else if (this.code.length - 1 == this.cursorLine) {
			List<String> temp = new ArrayList<>();
			temp.addAll(Arrays.asList(this.code));
			temp.add(line);
			this.code = temp.toArray(this.code);
		}
		this.cursorLine++;
	}

	public void updateLine(String line) {
		// System.out.println("Updating line #" + cursorLine + " to \"" + line +
		// "\"");
		this.code[cursorLine] = line;
	}

	public void setCursor(int line, boolean... args) {
		// System.out.println("Set cursor to line " + line + "!");
		this.cursorLine = line;
		if (args.length > 0 && args[0]) {
			sendEditScreen();
		}
	}

	public void setEditMode(EditMode e) {
		this.mode = e;
	}

	public EventType getEventType() {
		return this.et;
	}

	public void sendEditScreen() {
		owner.sendMessage(ChatColor.YELLOW + "-----------------------------------------------------");
		if (this.code.length == 0)
			owner.sendMessage(ChatColor.RED + "--->");
		for (int i = 0; i < this.code.length; i++) {
			if (code[i] != null)
				new FancyMessage("#" + (i + 1) + " ").color(ChatColor.GRAY)
						.tooltip(ChatColor.GRAY + "Click here to access the line menu").command("/script editor linemenu " + i)
						.then(this.code[i]).color(ChatColor.RED)
						.then((mode == EditMode.OVERWRITE && i == cursorLine) ? " <---" : "").color(ChatColor.RED)
						.send(owner);
			;
			if (i == cursorLine && mode == EditMode.INSERT)
				owner.sendMessage(ChatColor.RED + "--->");
		}
		owner.sendMessage(ChatColor.YELLOW + "-----------------------------------------------------");
	}

	public Script getScript() {
		if (et.isTimer())
			return new Script(this.name, getCode(), this.et, enabled, this.frequency, this.async);
		return new Script(this.name, getCode(), this.et, enabled);
	}

	public enum EditMode {
		INSERT, OVERWRITE;
	}

	@Override
	public int hashCode() {
		return owner.getName().hashCode();
	}

}
