package tk.ifutureserver.smartstaff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.ifutureserver.smartstaff.Main;
import tk.ifutureserver.smartstaff.util.TabComplete;

public class ViewRolesCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;

	public ViewRolesCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("viewroles").setTabCompleter(new TabComplete());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				//show all roles
				return true;
			} else if (args.length >1) {
				sender.sendMessage(ChatColor.RED+"Too many arguments!");
				return true;
			} else {
				return true;
			}
		}
		if (args.length < 1) {
			//show all roles
			return true;
		} else if (args.length >1) {
			sender.sendMessage(ChatColor.RED+"Too many arguments!");
			return true;
		} else {
			return true;
		}
	}
}