package me.yamakaja.bukkitjs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptException;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.yamakaja.bukkitjs.EventManager.EventType;
import me.yamakaja.bukkitjs.IngameEditor.EditMode;
import mkremins.fanciful.FancyMessage;

public class CommandScript implements CommandExecutor, Listener, TabCompleter {

	public String[] subcommands = { "help", "create", "edit", "execute", "delete", "list", "eventinfo", "sandbox",
			"sounds", "version", "reload" };

	public BukkitJs plugin;

	public HashMap<String, Byte> editmode = new HashMap<>();
	public HashMap<String, IngameEditor> editors = new HashMap<>();
	public HashMap<String, Integer> sounds = new HashMap<>();

	public static final int PAGE_SIZE = 10;
	private int pagesVar = 0;
	private boolean lastPageFlag = false;

	public static final long DAY_IN_TICKS = 20 * 60 * 60 * 24;
	public static final long HOUR_IN_TICKS = 20 * 60 * 60;
	public static final long MINUTE_IN_TICKS = 20 * 60;
	public static final long SECOND_IN_TICKS = 20;

	public CommandScript(BukkitJs plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		long starttime = System.currentTimeMillis();
		if (!sender.hasPermission("bukkitjs.use")) {
			sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
			return true;
		} else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
			plugin.scriptManager.reloadScripts();
			sender.sendMessage(ChatColor.GREEN + "Sucessfully reloaded scripts!");
			return true;
		} else if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command!");
			return true;
		}
		Player p = (Player) sender;

		if (args.length == 0) {
			displayHelp(p);
		} else if (args[0].equalsIgnoreCase("list")) {
			int page = 0;
			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
			}
			displayList(p, page);
		} else if (args[0].equalsIgnoreCase("create")) {
			if (args.length <= 2) {
				p.sendMessage(ChatColor.GOLD + "Please use the following syntax:");
				p.sendMessage(ChatColor.GOLD + "/script create <trigger> <name>");
				p.sendMessage(ChatColor.GOLD + "Protipp: You can use [Tab] auto-complete the trigger!");
			} else if (args.length >= 3) {
				if (plugin.scriptManager.getScriptByName(args[2]) == null) {
					try {
						if (args.length >= 4) {
							if (args.length == 5) {
								editors.put(p.getName(), new IngameEditor(p, args[2], "", EventType.valueOf(args[1]),
										Integer.parseInt(args[3]), Boolean.parseBoolean(args[4]), true));
							} else
								editors.put(p.getName(),
										new IngameEditor(p, args[2], "", EventType.valueOf(args[1]), true));
						} else
							editors.put(p.getName(),
									new IngameEditor(p, args[2], "", EventType.valueOf(args[1]), true));
						p.sendMessage("" + ChatColor.YELLOW + "You are now editing " + ChatColor.GOLD + "" + args[2]
								+ "" + ChatColor.YELLOW + ":");
						p.sendMessage("" + ChatColor.YELLOW + "Type " + ChatColor.DARK_RED + "/script save "
								+ ChatColor.YELLOW + "to exit and save.");
						editmode.put(p.getName(), (byte) 1);
					} catch (NumberFormatException e) {
						p.sendMessage(ChatColor.DARK_RED + "Incorrect number format!");
					} catch (IllegalArgumentException e) {
						p.sendMessage(ChatColor.DARK_RED + "Invalid trigger!");
					}
				} else {
					p.sendMessage(ChatColor.DARK_RED + "This script allready exists! Choose a different name!");
				}
			}
		} else if (args[0].equalsIgnoreCase("adjust")) {
			Script s;
			if (args.length >= 2) {
				if ((s = plugin.scriptManager.getScriptByName(args[1])) != null) {
					if (args.length == 2) {
						showTimeSelector(p, s.getName(), s.frequency);
					} else if (args.length == 3) {
						try {
							showTimeSelector(p, s.getName(), Long.parseLong(args[2]));
						} catch (NumberFormatException e) {
							showTimeSelector(p, s.getName(), s.frequency);
						}
					} else if (args.length == 4 && args[3].equalsIgnoreCase("submit")) {
						try {
							s.frequency = Long.parseLong(args[2]);
							plugin.scriptManager.saveScript(s);
							plugin.scriptManager.reloadScripts();
							displayList(p, 0);
						} catch (NumberFormatException e) {
						}
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("edit")) {
			if (args.length == 2) {
				Script s = plugin.scriptManager.getScriptByName(args[1]);
				if (s != null) {
					p.sendMessage("" + ChatColor.YELLOW + "You are now editing " + ChatColor.GOLD + "" + s.getName()
							+ "" + ChatColor.YELLOW + ":");
					p.sendMessage("" + ChatColor.YELLOW + "Type " + ChatColor.DARK_RED + "/script save "
							+ ChatColor.YELLOW + "to exit and save.");
					editors.put(p.getName(), new IngameEditor(p, s));
					editmode.put(p.getName(), (byte) 1);
				} else {
					p.sendMessage(ChatColor.DARK_RED + args[1]
							+ " does not exist! To create it use /script create <trigger> " + args[1] + ".");
				}
			}
		} else if (args[0].equalsIgnoreCase("execute")) {
			if (args.length == 2) {
				Script s = plugin.scriptManager.getScriptByName(args[1]);
				if (s != null && s.getEventType() == EventType.DUMMY) {
					p.sendMessage(ChatColor.GOLD + "Executing " + s.getName() + ChatColor.GOLD + "!");
					plugin.engineManager.execute(args[1], p);
				} else {
					p.sendMessage(ChatColor.GOLD + "" + args[1] + ChatColor.DARK_RED
							+ " does not exist or isn't a DUMMY script!");
				}
			}
		} else if (args[0].equalsIgnoreCase("save")) {
			if (editmode.containsKey(p.getName())) {
				plugin.scriptManager.saveScript(editors.get(p.getName()).getScript());
				plugin.scriptManager.reloadScripts();
				editors.remove(p.getName());
				editmode.remove(p.getName());
				p.sendMessage(ChatColor.YELLOW + "Closed editor and saved script!");
			} else {
				displayHelp(p);
			}
		} else if (args[0].equalsIgnoreCase("delete")) {
			if (args.length == 1) {
				p.sendMessage(ChatColor.GOLD + "Please use the following syntax:");
				p.sendMessage(ChatColor.GOLD + "/script delete <name>");
			} else if (args.length == 2) {
				Script s = plugin.scriptManager.getScriptByName(args[1]);
				if (s != null) {
					plugin.scriptManager.deleteScript(plugin.scriptManager.getScriptByName(args[1]));
					p.sendMessage(ChatColor.YELLOW + "Sucessfully deleted " + ChatColor.GOLD + args[1]
							+ ChatColor.YELLOW + "!");
				} else {
					p.sendMessage(ChatColor.GOLD + "" + args[1] + " " + ChatColor.DARK_RED + "does not exist!");
				}
			}
		} else if (args[0].equalsIgnoreCase("togglescript")) {
			Script s = plugin.scriptManager.getScriptByName(args[1]);
			int page = 0;
			try {
				page = Integer.parseInt(args[2]);
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
			}
			if (s != null)
				plugin.scriptManager.toggleScriptStatus(s);
			displayList(p, page);
		} else if (args[0].equalsIgnoreCase("sandbox")) {
			p.sendMessage(ChatColor.GOLD + "You are now in sandbox mode! Use exit() to exit.");
			editmode.put(p.getName(), (byte) 2);
		} else if (args[0].equalsIgnoreCase("ccs")) {
			new FancyMessage("This is a formatting test: ").color(ChatColor.AQUA).then("X").color(ChatColor.DARK_RED)
					.style(ChatColor.BOLD).suggest("/stop").send(p);
			p.sendMessage(ChatColor.DARK_BLUE + "&1 " + ChatColor.DARK_GREEN + "&2 " + ChatColor.DARK_AQUA + "&3 "
					+ ChatColor.DARK_RED + "&4 " + ChatColor.DARK_PURPLE + "&5 " + ChatColor.GOLD + "&6 "
					+ ChatColor.GOLD + "&7 " + ChatColor.DARK_GRAY + "&8 " + ChatColor.BLUE + "&9");
			p.sendMessage("" + ChatColor.GREEN + "&a " + ChatColor.AQUA + "&b " + ChatColor.RED + "&c "
					+ ChatColor.LIGHT_PURPLE + "&d " + ChatColor.YELLOW + "&e " + ChatColor.WHITE + "&f");
		} else if (args[0].equalsIgnoreCase("editor")) {
			if (editors.containsKey(p.getName()) && args.length == 3) {
				switch (args[1]) {
				case "overwrite":
					editors.get(p.getName()).setEditMode(EditMode.OVERWRITE);
					try {
						editors.get(p.getName()).setCursor(Integer.parseInt(args[2]), true);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					break;
				case "line":
					try {
						editors.get(p.getName()).setCursor(Integer.parseInt(args[2]), true);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					break;
				case "delete":
					try {
						editors.get(p.getName()).deleteLine(Integer.parseInt(args[2]));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					break;
				case "linemenu":
					try {
						int i = Integer.parseInt(args[2]);
						IngameEditor editor = editors.get(p.getName());
						new FancyMessage("#" + (i + 1) + ": ").color(ChatColor.GRAY).then("X").color(ChatColor.DARK_RED)
								.style(ChatColor.BOLD).command("/script editor delete " + i).then(",")
								.color(ChatColor.GRAY).then("F").color(ChatColor.AQUA).style(ChatColor.BOLD)
								.command("/script editor line " + i).then(",").color(ChatColor.GRAY).then("O")
								.color(ChatColor.RED).style(ChatColor.BOLD).command("/script editor overwrite " + i)
								.then(",").color(ChatColor.GRAY).then("C").color(ChatColor.GREEN).style(ChatColor.BOLD)
								.suggest(editor.getCode(i)).send(p);

					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		} else if (args[0].equalsIgnoreCase("eventinfo")) {
			if (!(args.length >= 2)) {
				p.sendMessage(ChatColor.GOLD + "Please use the following syntax:");
				p.sendMessage(ChatColor.GOLD + "/script eventinfo <event>");
			}
			Class<?> clazz = null;
			int page = 0;
			try {
				if (args.length >= 3) {
					page = Integer.parseInt(args[2]);
				}
			} catch (NumberFormatException ex) {
			}
			int mode = 0;
			try {
				if (args.length >= 4) {
					mode = Integer.parseInt(args[3]);
				}
			} catch (NumberFormatException ex) {
			}
			try {
				clazz = EventType.valueOf(args[1]).clazz;
			} catch (IllegalArgumentException e) {
				try {
					clazz = Class.forName(args[1]);
				} catch (ClassNotFoundException ex) {
					p.sendMessage(ChatColor.DARK_RED + "Unkown Event!");
					p.sendMessage(
							"" + ChatColor.GREEN + "Tipp: " + ChatColor.AQUA + "You can use [Tab] to autocomplete.");
					return true;
				}

			}
			p.sendMessage("" + ChatColor.YELLOW + "---------------------------------------------------");
			p.sendMessage(
					"" + ChatColor.YELLOW + "    " + ChatColor.RED + "Classviewer: " + ChatColor.GOLD + " " + args[1]);

			FancyMessage modeSelect = new FancyMessage("------ ").color(ChatColor.YELLOW).then("Methods")
					.color(ChatColor.DARK_GREEN);
			int lines = 0;
			switch (mode) {
			case 0:
				modeSelect.style(ChatColor.UNDERLINE).then(" ").then("Fields").color(ChatColor.BLUE)
						.tooltip(ChatColor.GOLD + "Switch to " + ChatColor.BLUE + "Field " + ChatColor.GOLD + "view")
						.command("/script eventinfo " + args[1] + " 0 1").then(" ").then("Constructor")
						.color(ChatColor.DARK_AQUA)
						.tooltip(ChatColor.GOLD + "Switch to " + ChatColor.DARK_AQUA + "Constructor " + ChatColor.GOLD
								+ "view")
						.command("/script eventinfo " + args[1] + " 0 2").then(" ------------------")
						.color(ChatColor.YELLOW).send(p);
				p.sendMessage("" + ChatColor.YELLOW + "---------------------------------------------------");

				Method[] m = clazz.getMethods();
				for (int i : getIndexRangeOnPage(page, m.length)) {
					lines++;
					FancyMessage message = new FancyMessage(" - ").color(ChatColor.YELLOW).then(m[i].getName() + "(")
							.color(ChatColor.DARK_GREEN);
					for (Parameter pa : m[i].getParameters()) {
						message.then(pa.getType().getSimpleName())
								.tooltip(ChatColor.GRAY + "Click here for more information on " + ChatColor.RED
										+ pa.getType().getSimpleName())
								.color(ChatColor.RED).command("/script eventinfo " + pa.getType().getName());
						if (pa != m[i].getParameters()[m[i].getParameterCount() - 1])
							message.then(", ").color(ChatColor.GRAY);
					}
					message.then(")").color(ChatColor.DARK_GREEN).then(" : ").color(ChatColor.GRAY)
							.then(m[i].getReturnType().getSimpleName()).color(ChatColor.RED)
							.tooltip(ChatColor.GRAY + "Click here for more information on " + ChatColor.RED
									+ m[i].getReturnType().getSimpleName())
							.command("/script eventinfo " + m[i].getReturnType().getName()).send(p);
				}
				break;
			case 1:
				modeSelect
						.tooltip(ChatColor.GOLD + "Switch to " + ChatColor.DARK_GREEN + "Method " + ChatColor.GOLD
								+ "view")
						.command("/script eventinfo " + args[1] + " 0 0").then(" ").then("Fields").color(ChatColor.BLUE)
						.style(ChatColor.UNDERLINE).then(" ").then("Constructor").color(ChatColor.DARK_AQUA)
						.tooltip(ChatColor.GOLD + "Switch to " + ChatColor.DARK_AQUA + "Constructor " + ChatColor.GOLD
								+ "view")
						.command("/script eventinfo " + args[1] + " 0 2").then(" ------------------")
						.color(ChatColor.YELLOW).send(p);
				p.sendMessage("" + ChatColor.YELLOW + "---------------------------------------------------");
				Field[] f = clazz.getFields();
				for (int i : getIndexRangeOnPage(page, f.length)) {
					lines++;
					new FancyMessage(" - ").color(ChatColor.YELLOW).then(f[i].getName()).color(ChatColor.BLUE)
							.then(" : ").color(ChatColor.GRAY).then(f[i].getType().getSimpleName()).color(ChatColor.RED)
							.tooltip(ChatColor.GRAY + "Click here for more information on " + ChatColor.RED
									+ f[i].getType().getSimpleName())
							.command("/script eventinfo " + f[i].getType().getName()).send(p);
				}
				break;
			case 2:
				modeSelect
						.tooltip(ChatColor.GOLD + "Switch to " + ChatColor.DARK_GREEN + "Method " + ChatColor.GOLD
								+ "view")
						.command("/script eventinfo " + args[1] + " 0 0").then(" ").then("Fields").color(ChatColor.BLUE)
						.tooltip(ChatColor.GOLD + "Switch to " + ChatColor.BLUE + "Field " + ChatColor.GOLD + "view")
						.command("/script eventinfo " + args[1] + " 0 1").then(" ").then("Constructors")
						.color(ChatColor.DARK_AQUA).style(ChatColor.UNDERLINE).then(" ------------------")
						.color(ChatColor.YELLOW).send(p);
				p.sendMessage("" + ChatColor.YELLOW + "---------------------------------------------------");
				Constructor<?>[] c = clazz.getConstructors();
				for (int i : getIndexRangeOnPage(page, c.length)) {
					lines++;
					FancyMessage message = new FancyMessage(" - ").color(ChatColor.YELLOW)
							.then(clazz.getSimpleName() + "(").color(ChatColor.DARK_GREEN);
					for (Parameter pa : c[i].getParameters()) {
						message.then(pa.getType().getSimpleName())
								.tooltip(ChatColor.GRAY + "Click here for more information on " + ChatColor.RED
										+ pa.getType().getSimpleName())
								.color(ChatColor.RED).command("/script eventinfo " + pa.getType().getName());
						if (pa != c[i].getParameters()[c[i].getParameterCount() - 1])
							message.then(", ").color(ChatColor.GRAY);
					}
					message.then(")").color(ChatColor.DARK_GREEN).send(p);
				}
				break;
			}

			for (; lines < PAGE_SIZE; lines++)
				p.sendMessage("");

			getPageSelector(page, "/script eventinfo " + args[1] + " " + (page - 1) + " " + mode,
					"/script eventinfo " + args[1] + " " + (page + 1) + " " + mode).send(p);

			p.sendMessage("" + ChatColor.YELLOW + "---------------------------------------------------");
		} else if (args[0].equalsIgnoreCase("sounds")) {
			if (args.length == 1) {
				displaySounds(p, 0);
			} else {
				try {
					if (args.length == 2)
						p.playSound(p.getLocation(), Sound.valueOf(args[1]), 1, 1);
					if (args.length == 3) {
						try {
							p.playSound(p.getLocation(), Sound.valueOf(args[1]), 1, Float.parseFloat(args[2]));
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException();
						}
					}
				} catch (IllegalArgumentException e) {
					try {
						displaySounds(p, Integer.parseInt(args[1]));
					} catch (NumberFormatException ex) {
						displaySounds(p, 0);
					}
				}
			}

		} else {
			displayHelp(p);
		}

		if (plugin.debug)
			System.out.println("Processing command took " + (System.currentTimeMillis() - starttime) + "ms");
		return true;
	}

	public int[] getIndexRangeOnPage(int page, int items) {
		pagesVar = (int) Math.ceil((double) items / (double) PAGE_SIZE);
		if (items == 0 || page * PAGE_SIZE == items) {
			lastPageFlag = true;
			return new int[0];
		}
		int startindex = page * PAGE_SIZE, endindex;
		if (page == (int) (Math.floor(items / PAGE_SIZE))) {
			endindex = startindex + (items % PAGE_SIZE);
			lastPageFlag = true;
		} else
			endindex = startindex + PAGE_SIZE;
		// System.out.println("new int[" + endindex + " - " + startindex +
		// "];");
		int[] result = new int[endindex - startindex];
		for (int i = startindex; i < endindex; i++) {
			// System.out.println("result[" + i % PAGE_SIZE + "] = " + i);
			result[i % PAGE_SIZE] = i;
		}
		return result;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		List<String> result = new ArrayList<>();

		if (args.length == 0)
			return null;
		else if (args.length == 1) {
			for (String subcommand : subcommands)
				if (subcommand.startsWith(args[0]))
					result.add(subcommand);
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("eventinfo")) {
				for (EventType e : EventType.values())
					if (e.name().toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(e.name());
			} else if (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("delete")) {
				for (Script s : plugin.scriptManager.scripts)
					if (s.getName().toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(s.getName());
			} else if (args[0].equalsIgnoreCase("execute")) {
				for (Script s : plugin.scriptManager.scripts)
					if (s.getEventType().isDummy() && s.getName().toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(s.getName());
			} else if (args[0].equalsIgnoreCase("sounds")) {
				for (Sound s : Sound.values()) {
					if (s.name().toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(s.name());
				}
			}
		}

		return result;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (editmode.containsKey(e.getPlayer().getName())) {
			e.setCancelled(true);
			if (editmode.get(e.getPlayer().getName()) == (byte) 1) {
				editors.get(e.getPlayer().getName()).processLine(e.getMessage());
			} else if (editmode.get(e.getPlayer().getName()) == (byte) 2) {
				if (e.getMessage().equalsIgnoreCase("exit()")) {
					e.getPlayer().sendMessage(ChatColor.GOLD + "You can now chat normally again!");
					editmode.remove(e.getPlayer().getName());
					return;
				}
				try {
					plugin.engineManager.eval(e.getMessage());
				} catch (ScriptException e1) {
					e.getPlayer().sendMessage(ChatColor.RED + "Something went wrong! Error:");
					e.getPlayer().sendMessage(ChatColor.RED + e1.getCause().getLocalizedMessage());
				}
			}
		}
	}

	public void displayList(Player p, int page) {
		p.sendMessage("" + ChatColor.YELLOW + "--------- " + ChatColor.WHITE + "Active Scripts " + ChatColor.YELLOW
				+ "---------------------------");
		p.sendMessage(ChatColor.GOLD + "Below is a list of all active scripts:");
		ArrayList<Script> script = plugin.scriptManager.scripts;
		for (int i : getIndexRangeOnPage(page, script.size())) {
			// p.sendMessage("" + ChatColor.YELLOW + " - " + ChatColor.GOLD + ""
			// + script.getName() + " " + ChatColor.RED + "@ " + ChatColor.BLUE
			// + "" +
			// script.getEventType().name());
			new FancyMessage(" - ").color(ChatColor.YELLOW).then(script.get(i).enabled ? "0--" : "--0")
					.color(getSwitchColor(script.get(i))).tooltip(getSwitchTooltip(script.get(i)))
					.command(script.get(i).getEventType().isDummy() ? ""
							: "/script togglescript " + script.get(i).getName() + " " + page)
					.style(ChatColor.BOLD).then(" ").then(script.get(i).getName()).color(ChatColor.GOLD).then(" @ ")
					.color(ChatColor.RED).then(script.get(i).getEventType().name()).color(ChatColor.BLUE).then(" ")
					.then("EDIT").color(ChatColor.RED).command("/script edit " + script.get(i).getName())
					.tooltip(ChatColor.GRAY + "Click here to edit " + ChatColor.GOLD + script.get(i).getName())
					.then(" ").then(script.get(i).getEventType().isTimer() ? "ADJ" : "").color(ChatColor.GOLD)
					.tooltip(ChatColor.GOLD + "Click here to adjust the frequency at which this",
							ChatColor.GOLD + "script gets fired.")
					.command("/script adjust " + script.get(i).getName())
					.then(script.get(i).getEventType().isTimer() ? " " : "").then("DEL").color(ChatColor.DARK_RED)
					.suggest("/script delete " + script.get(i).getName())
					.tooltip(ChatColor.GRAY + "Click here to delete " + ChatColor.GOLD + script.get(i).getName())
					.send(p);
		}
		new FancyMessage(" - ").color(ChatColor.YELLOW).then("New").color(ChatColor.GREEN).style(ChatColor.BOLD)
				.tooltip(ChatColor.GOLD + "Create a new script").suggest("/script create ").send(p);
		getPageSelector(page, "/script list " + (page - 1), "/script list " + (page + 1)).send(p);
		p.sendMessage("" + ChatColor.YELLOW + "---------------------------------------------------");
	}

	public void displayHelp(Player p) {
		p.sendMessage("" + ChatColor.YELLOW + "---------- " + ChatColor.WHITE + "Script Help " + ChatColor.YELLOW
				+ "-----------------------------");
		p.sendMessage(ChatColor.GOLD + "Below is a list of all commands:");
		new FancyMessage("/script help").color(ChatColor.GOLD).then(": Displays this help message.").send(p);
		new FancyMessage("/script list").command("/script list").tooltip(ChatColor.GOLD + "Click to execute")
				.color(ChatColor.GOLD).then(": Lists all active Scripts.").send(p);
		new FancyMessage("/script create ").color(ChatColor.GOLD).suggest("/script create ")
				.tooltip(ChatColor.GOLD + "Click to prepair execution").then("<trigger>").color(ChatColor.GOLD)
				.tooltip(ChatColor.GOLD + "The trigger tells the script when to launch.",
						ChatColor.GOLD + "There are there types of triggers: " + ChatColor.RED + "Events"
								+ ChatColor.GOLD + ", " + ChatColor.RED + "DUMMY" + ChatColor.GOLD + " and "
								+ ChatColor.RED + "TIMER" + ChatColor.GOLD + ".",
						ChatColor.GOLD + "The " + ChatColor.RED + "Event" + ChatColor.GOLD
								+ "-triggers launch with their corresponding Event,",
						ChatColor.GOLD + "for example AsyncPlayerChatEvent launches when a player sends out a message.",
						"" + ChatColor.RED + "DUMMY" + ChatColor.GOLD
								+ " triggered scripts can be launched using a command:",
						ChatColor.GOLD + "/script execute <Name (of a " + ChatColor.RED + "DUMMY" + ChatColor.GOLD
								+ " script)>",
						ChatColor.GOLD + "Note that " + ChatColor.RED + "DUMMY " + ChatColor.GOLD
								+ "scripts can access the executing player",
						ChatColor.GOLD + "using the \"player\" variable.",
						"" + ChatColor.RED + "TIMER" + ChatColor.GOLD
								+ "-scripts trigger every n ticks (20 tick = 1 second) and require two",
						ChatColor.GOLD + "extra parameters:",
						ChatColor.GOLD + "/script create TIMER <name> <frequency in ticks> <async: (true|false)>",
						ChatColor.GOLD + "(Async means it wont wait for your world to tick and allways trigger every",
						ChatColor.GOLD + "n/20 seconds where as non-async will wait and also slow down with your tps",
						ChatColor.GOLD + "10 tps -> half speed)")
				.then(" ").then("<name>").color(ChatColor.GOLD)
				.tooltip(ChatColor.GOLD + "Just an ordinary old name ...")
				.then(": Main command used to create Scripts.").send(p);
		new FancyMessage("/script edit ").color(ChatColor.GOLD).suggest("/script edit ")
				.tooltip(ChatColor.GOLD + "Click to prepair execution").then("<name>").color(ChatColor.GOLD)
				.tooltip(ChatColor.GOLD + "Your scripts name.", ChatColor.GOLD + "Protipp: Use [Tab] to auto complete!")
				.then(": Used to edit scripts.").send(p);
		new FancyMessage("/script execute ").suggest("/script execute ")
				.tooltip(ChatColor.GOLD + "Click to prepair execution").color(ChatColor.GOLD).then("<name>")
				.tooltip(ChatColor.GOLD + "The name of a DUMMY script.", ChatColor.GOLD + "Autocomplete FTW ;)")
				.color(ChatColor.GOLD).then(": Allows you to execute DUMMY scripts.").send(p);
		new FancyMessage("/script eventinfo ").color(ChatColor.GOLD).suggest("/script eventinfo ")
				.tooltip(ChatColor.GOLD + "Click to prepair execution").then("<event>").color(ChatColor.GOLD)
				.tooltip(ChatColor.GOLD + "An event.",
						ChatColor.GOLD + "Remember: Using autocomplete ([Tab]) is allways a good idea :).")
				.then(": Provides more information on the specified event.").send(p);
		new FancyMessage("/script version").tooltip(ChatColor.GOLD + "Click to execute").command("/script version")
				.color(ChatColor.GOLD).then(": General plugin information.").send(p);
		// p.sendMessage("" + ChatColor.YELLOW +
		// "---------------------------------------------------");
	}

	public FancyMessage getPageSelector(int page, String commandLeft, String commandRight) {
		FancyMessage pageselector = new FancyMessage("    ");
		if (page == 0)
			pageselector.then("<==").color(ChatColor.GRAY);
		else
			pageselector.then("<==").color(ChatColor.GOLD)
					.tooltip(ChatColor.GRAY + "Go to page " + ChatColor.GOLD + (page)).command(commandLeft);

		pageselector.then(" " + (page + 1)).color(ChatColor.AQUA)
				.tooltip(ChatColor.GRAY + "You are currently on page " + ChatColor.GOLD + "" + (page + 1)
						+ ChatColor.GOLD + " of " + ChatColor.GOLD + "" + pagesVar)
				.then("/").color(ChatColor.GRAY)
				.tooltip(ChatColor.GRAY + "You are currently on page " + ChatColor.GOLD + "" + (page + 1)
						+ ChatColor.GOLD + " of " + ChatColor.GOLD + "" + pagesVar)
				.then(pagesVar + " ").color(ChatColor.AQUA).tooltip(ChatColor.GRAY + "You are currently on page "
						+ ChatColor.GOLD + "" + (page + 1) + ChatColor.GOLD + " of " + ChatColor.GOLD + "" + pagesVar);

		if (lastPageFlag)
			pageselector.then("==>").color(ChatColor.GRAY);
		else
			pageselector.then("==>").color(ChatColor.GOLD)
					.tooltip(ChatColor.GRAY + "Go to page " + ChatColor.GOLD + (page + 2)).command(commandRight);
		lastPageFlag = false;
		return pageselector;
	}

	public void showTimeSelector(Player p, String script, long current) {
		p.sendMessage("" + ChatColor.YELLOW + "-------- " + ChatColor.WHITE + "Adjust frequency for " + ChatColor.GOLD
				+ " " + script + "" + ChatColor.YELLOW + " ------");
		p.sendMessage("");
		p.sendMessage("");
		int days = getFlooredValue(current, DAY_IN_TICKS), hours = getFlooredValue(current, HOUR_IN_TICKS),
				minutes = getFlooredValue(current, MINUTE_IN_TICKS),
				seconds = getFlooredValue(current, SECOND_IN_TICKS), ticks = getFlooredValue(current);
		new FancyMessage("            ").then("\u25b2").color(ChatColor.GOLD)
				.tooltip(ChatColor.GOLD + "Click to increase " + ChatColor.GOLD + "Days" + ChatColor.GOLD + " by 1")
				.command("/script adjust " + script + " " + (current + DAY_IN_TICKS)).then(" ").then("\u25b2")
				.color(ChatColor.GOLD)
				.tooltip(ChatColor.GOLD + "Click to increase " + ChatColor.GOLD + "Hours" + ChatColor.GOLD + " by 1")
				.command("/script adjust " + script + " " + (current + HOUR_IN_TICKS)).then(" ").then("\u25b2")
				.color(ChatColor.GOLD)
				.tooltip(ChatColor.GOLD + "Click to increase " + ChatColor.GOLD + "Minutes" + ChatColor.GOLD + " by 1")
				.command("/script adjust " + script + " " + (current + MINUTE_IN_TICKS)).then(" ").then("\u25b2")
				.color(ChatColor.GOLD)
				.tooltip(ChatColor.GOLD + "Click to increase " + ChatColor.GOLD + "Seconds" + ChatColor.GOLD + " by 1")
				.command("/script adjust " + script + " " + (current + SECOND_IN_TICKS)).then(" ").then("\u25b2")
				.color(ChatColor.GOLD)
				.tooltip(ChatColor.GOLD + "Click to increase " + ChatColor.GOLD + "Ticks" + ChatColor.GOLD + " by 1")
				.command("/script adjust " + script + " " + (current + 1)).send(p);
		new FancyMessage("Frequency: ").color(ChatColor.YELLOW).then(String.format("%02d", days)).color(ChatColor.AQUA)
				.tooltip(ChatColor.GOLD + "Days").then(":").then(String.format("%02d", hours)).color(ChatColor.AQUA)
				.tooltip(ChatColor.GOLD + "Hours").then(":").then(String.format("%02d", minutes)).color(ChatColor.AQUA)
				.tooltip(ChatColor.GOLD + "Minutes").then(":").then(String.format("%02d", seconds))
				.color(ChatColor.AQUA).tooltip(ChatColor.GOLD + "Seconds").then(":").then(String.format("%02d", ticks))
				.color(ChatColor.AQUA).tooltip(ChatColor.GOLD + "Ticks").then("  ").then("SUBMIT")
				.color(current >= 5 ? ChatColor.GREEN : ChatColor.RED).style(ChatColor.BOLD)
				.tooltip(current >= 5 ? ChatColor.GOLD + "Click to submit changes"
						: "" + ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Value cannot be below 5!")
				.command(current >= 5 ? "/script adjust " + script + " " + current + " submit" : "").send(p);
		new FancyMessage("            ").then("\u25bc").color(days > 0 ? ChatColor.GOLD : ChatColor.GRAY)
				.tooltip(current >= DAY_IN_TICKS
						? ChatColor.GOLD + "Click to decrease " + ChatColor.GOLD + "Days" + ChatColor.GOLD + " by 1"
						: "" + ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Error negative limit reached!")
				.command(current >= DAY_IN_TICKS ? "/script adjust " + script + " " + (current - DAY_IN_TICKS) : "")
				.then(" ").then("\u25bc").color(current >= HOUR_IN_TICKS ? ChatColor.GOLD : ChatColor.GRAY)
				.tooltip(current >= HOUR_IN_TICKS
						? ChatColor.GOLD + "Click to decrease " + ChatColor.GOLD + "Hours" + ChatColor.GOLD + " by 1"
						: "" + ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Error negative limit reached!")
				.command(current >= HOUR_IN_TICKS ? "/script adjust " + script + " " + (current - HOUR_IN_TICKS) : "")
				.then(" ").then("\u25bc").color(current >= MINUTE_IN_TICKS ? ChatColor.GOLD : ChatColor.GRAY)
				.tooltip(current >= MINUTE_IN_TICKS
						? ChatColor.GOLD + "Click to decrease " + ChatColor.GOLD + "Minutes" + ChatColor.GOLD + " by 1"
						: "" + ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Error negative limit reached!")
				.command(current >= MINUTE_IN_TICKS ? "/script adjust " + script + " " + (current - MINUTE_IN_TICKS)
						: "")
				.then(" ").then("\u25bc").color(current >= SECOND_IN_TICKS ? ChatColor.GOLD : ChatColor.GRAY)
				.tooltip(current >= SECOND_IN_TICKS
						? ChatColor.GOLD + "Click to decrease " + ChatColor.GOLD + "Seconds" + ChatColor.GOLD + " by 1"
						: "" + ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Error negative limit reached!")
				.command(current >= SECOND_IN_TICKS ? "/script adjust " + script + " " + (current - SECOND_IN_TICKS)
						: "")
				.then(" ").then("\u25bc").color(current > 0 ? ChatColor.GOLD : ChatColor.GRAY)
				.tooltip(current > 0
						? ChatColor.GOLD + "Click to decrease " + ChatColor.GOLD + "Ticks" + ChatColor.GOLD + " by 1"
						: "" + ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Error negative limit reached!")
				.command(current > 0 ? "/script adjust " + script + " " + (current - 1) : "").send(p);
		p.sendMessage("");
		p.sendMessage("");
		p.sendMessage("" + ChatColor.YELLOW + "---------------------------------------------------");
	}

	public int getFlooredValue(long dividend, long divisor) {
		if (divisor == DAY_IN_TICKS)
			return (int) (Math.floorDiv(dividend, divisor));
		else if (divisor == HOUR_IN_TICKS)
			return (int) (Math.floorDiv(dividend % DAY_IN_TICKS, divisor));
		else if (divisor == MINUTE_IN_TICKS)
			return (int) (Math.floorDiv(dividend % HOUR_IN_TICKS, divisor));
		else if (divisor == SECOND_IN_TICKS)
			return (int) (Math.floorDiv(dividend % MINUTE_IN_TICKS, divisor));
		return 0;
	}

	public int getFlooredValue(long value) {
		return (int) (Math.floor(value % 20L));
	}

	public ChatColor getSwitchColor(Script s) {
		if (s.getEventType().isDummy())
			return ChatColor.GRAY;
		else
			return s.isEnabled() ? ChatColor.GREEN : ChatColor.DARK_RED;
	}

	public String getSwitchTooltip(Script s) {
		if (!s.getEventType().isDummy()) {
			return s.enabled
					? ChatColor.GOLD + "Click to " + ChatColor.DARK_RED + "disable " + ChatColor.GOLD + "this script"
					: ChatColor.GOLD + "Click to " + ChatColor.GREEN + "enable " + ChatColor.GOLD + "this script";
		} else
			return ChatColor.DARK_RED + "You cannot disable dummy scripts...";
	}

	public void displaySounds(Player p, int page) {

		p.sendMessage("" + ChatColor.YELLOW + "--------------- " + ChatColor.WHITE + "Sound Browser" + ChatColor.YELLOW
				+ " ---------------------");

		Sound[] sounds = Sound.values();

		for (int i : getIndexRangeOnPage(page, sounds.length)) {
			new FancyMessage(" - ").color(ChatColor.YELLOW).then(sounds[i].name()).color(ChatColor.GOLD).then(" ")
					.then("\u25ba").color(ChatColor.GREEN).style(ChatColor.BOLD)
					.command("/script sounds " + sounds[i].name())
					.tooltip(ChatColor.GOLD + "Click here to play this sound!").send(p);
		}

		getPageSelector(page, "/script sounds " + (page - 1), "/script sounds " + (page + 1)).send(p);

		p.sendMessage("" + ChatColor.YELLOW + "---------------------------------------------------");
	}

}
