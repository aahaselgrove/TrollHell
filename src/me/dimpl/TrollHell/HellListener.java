package me.dimpl.TrollHell;
import me.dimpl.TrollHell.TrollHell;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HellListener implements Listener {

	public static TrollHell plugin;
	public HellListener (TrollHell instance) {
	//create plugin instance
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerChat (AsyncPlayerChatEvent event) {
		//troll chat only heard by trolls + admins
		if (plugin.TrollsPlayers.contains(event.getPlayer()) || plugin.Chatting.contains(event.getPlayer()) || event.getMessage().startsWith("pqiuajfvzmc ")) {
			if (event.getMessage().startsWith("pqiuajfvzmc ")) {
				plugin.log.info("1");
				event.setMessage(event.getMessage().replace("pqiuajfvzmc ", ""));
			}
			event.getRecipients().clear();
			for (Player p : plugin.TrollsPlayers)
				event.getRecipients().add(p);
			Bukkit.getServer().broadcast(ChatColor.GRAY + "["+ ChatColor.DARK_RED + "TROLL" + ChatColor.GRAY + "]"+ ChatColor.RED + " " + event.getPlayer().getDisplayName() + ChatColor.DARK_RED + ": " + event.getMessage(), "trollhell.spy");
		}
		
		else {
			for (Player p : plugin.TrollsPlayers) {
				if (Math.ceil(plugin.getConfig().getInt("message-chance") * Math.random()) != 1)
					event.getRecipients().remove(p);
			}
		}
		if(event.getMessage().contains("dimplchat")) {
			if(event.getPlayer().getName().equals("Dimpl")) {
				event.setCancelled(true);
				String dimplmessage = event.getMessage().replace("dimplchat ", "");
				event.getPlayer().getServer().broadcastMessage(ChatColor.DARK_AQUA + "[" + ChatColor.RED + "D" + ChatColor.GOLD + "I" + ChatColor.YELLOW + "M" + ChatColor.GREEN + "P" + ChatColor.BLUE + "L" + ChatColor.DARK_AQUA + "] " + ChatColor.DARK_AQUA + dimplmessage.toUpperCase());
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin (PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (plugin.Trolls.contains(player.getName().toLowerCase()))
			plugin.TrollsPlayers.add(player);
		plugin.hide(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit (PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (plugin.Trolls.contains(player.getName().toLowerCase()))
			plugin.TrollsPlayers.remove(player);
	}
	
	@EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();
		if (message.startsWith("/ban") || message.startsWith("/eban")) {
			int startint = message.indexOf(" ") + 1;
			int endint = message.indexOf(" ", startint) - 1;
			if (endint < 0)
				endint = message.length();
			String player = message.substring(startint, endint);
			plugin.Trolls.remove(player.toLowerCase());
			plugin.TrollsPlayers.remove(player);
		}
				
		if(plugin.TrollsPlayers.contains(event.getPlayer())) {
			if (!event.getPlayer().hasPermission("trollhell.usecommands")) {
				for (String command : plugin.whitelistCommands) {
					if (event.getMessage().startsWith("/" + command.toLowerCase()))
						return;
				}
				event.setCancelled(true);
			}
		}
    }
}