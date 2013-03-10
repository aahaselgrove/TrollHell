package me.dimpl.TrollHell;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.dimpl.TrollHell.HellListener;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TrollHell extends JavaPlugin{

	
	public final Logger log = Logger.getLogger("Minecraft");
	private final HellListener listener = new HellListener(this);
	public volatile List<String> Trolls = new ArrayList<String>();
	public volatile List<Player> TrollsPlayers = new ArrayList<Player>();
	public String[] whitelistCommands;
	public String whitelistCommandsString;
	public volatile List<Player> Chatting = new ArrayList<Player>();
	String RED = ChatColor.RED.toString();
	String DARKRED = ChatColor.DARK_RED.toString();
	CommandSender Console;
	
	@Override
	public void onEnable() {
		//plugin enabled
		PluginDescriptionFile pdffile = this.getDescription();
		Console = getServer().getConsoleSender();
		Console.sendMessage(ChatColor.AQUA + pdffile.getName() + " version " + pdffile.getVersion() + " is enabled.");
		//set up listener
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(listener, this);
		this.saveDefaultConfig();
		whitelistCommandsString = this.getConfig().getString("whitelist-commands");
		whitelistCommands = whitelistCommandsString.split(", ");
		Trolls = this.getConfig().getStringList("trolls");
		Console.sendMessage(ChatColor.GREEN + "Trolls loaded from config.");
	}
	
	@Override
	public void onDisable() {
		//plugin disabled
		PluginDescriptionFile pdffile = this.getDescription();
		Console.sendMessage(ChatColor.AQUA + pdffile.getName() + " is now disabled.");
		this.getConfig().set("trolls", Trolls);
		this.saveConfig();
		Console.sendMessage(ChatColor.GREEN + "Trolls saved to config.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equals("hell")) {
			if (args.length == 0) {
				sender.sendMessage(RED + "Please specify a player.");
				return false;
			}
			else if (args.length == 1) {
				Player player = getServer().getPlayer(args[0]);
				if (player != null) {
					//the main step
					troll(player, sender);
					return true;
				}
				else sender.sendMessage(RED + "Player is offline.");
				return false;
			}
			else return false;
		}
		
		if (cmd.getName().equals("trolls")) {
			if (args.length == 0) {
				if (!TrollsPlayers.isEmpty()) {
					StringBuilder trolls = new StringBuilder();
					trolls.append(RED + "Trolls:" + ChatColor.GOLD);
					for (Player p : TrollsPlayers)
						trolls.append(" " + p.getDisplayName() + ",");
					trolls.deleteCharAt(trolls.length()-1);
					sender.sendMessage(trolls.toString());
					return true;
				}
				else sender.sendMessage(DARKRED + "No trolls are online.");
				return true;
			}
		}
				
		if (cmd.getName().equals("helltalk")) {
			if (sender.getName() != "CONSOLE") {
				if (args.length == 0) {
					if (!Chatting.contains(sender)) {
						Chatting.add((Player) sender);
						sender.sendMessage(DARKRED + "Talking to trolls.");
						return true;
					}
					else {
						Chatting.remove((Player) sender);
						sender.sendMessage(RED + "No longer talking to trolls.");
						return true;
					}
				}
				else {
					String message = "pqiuajfvzmc " + StringUtils.join(args, " ");
					((Player) sender).chat(message);
					return true;
				}
			}
			else {
				Console.sendMessage(RED + "Use /say.");
				return true;
			}
		}
		else return false;
	}
	
	public void troll (Player player, CommandSender sender) {
		//add troll to array
		if (!Trolls.contains(player.getName().toLowerCase())) {
			Trolls.add(player.getName().toLowerCase());
			TrollsPlayers.add(player);
			hide(player);
			Console.sendMessage(DARKRED + player.getDisplayName() + " has been placed in hell!!");
			if (sender.getName() == "CONSOLE")
				return;
			sender.sendMessage(DARKRED + player.getDisplayName() + " has been placed in hell!!");
		}
		//remove troll
		else {
			unhide(player);
			TrollsPlayers.remove(player);
			Trolls.remove(player.getName().toLowerCase());
			Console.sendMessage(RED + player.getDisplayName() + " has been removed from hell!!");
			if (sender.getName() == "CONSOLE")
				return;
			sender.sendMessage(RED + player.getDisplayName() + " has been removed from hell!!");
		}
	}
	
	public void hide (Player player) {
		//hide players from troll
		if (TrollsPlayers.contains(player)) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!(TrollsPlayers.contains(p) || p.hasPermission("trollhell.spy"))) {
					p.hidePlayer(player);
					player.hidePlayer(p);
				}
			}
		}
		//hide trolls from player
		else {
			if (!player.hasPermission("trollhell.spy")) {
				for (Player p : TrollsPlayers) {
					p.hidePlayer(player);
					player.hidePlayer(p);
				}
			}
		}
	}
	
	public void unhide (Player player) {
		//unhide players from troll
		if (TrollsPlayers.contains(player)) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!(TrollsPlayers.contains(p) || p.hasPermission("trollhell.spy"))) {
					p.showPlayer(player);
					player.showPlayer(p);
				}
			}
		}
		//unhide trolls from player
		else {
			if (!player.hasPermission("trollhell.spy")) {
				for (Player p : TrollsPlayers) {
					p.showPlayer(player);
					player.showPlayer(p);
				}
			}
		}
	}
}