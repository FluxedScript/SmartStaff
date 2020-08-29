package tk.ifutureserver.fluxedscript.smartstaff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import tk.ifutureserver.fluxedscript.smartstaff.Main;
import tk.ifutureserver.fluxedscript.smartstaff.util.TabComplete;

public class ViewRolesCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;

	public ViewRolesCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("viewroles").setExecutor(this);
		plugin.getCommand("viewroles").setTabCompleter(new TabComplete());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(String.join("\n", StaffModeCommand.getAllRanks()));
			return true;
		} else if (args.length >1) {
			sender.sendMessage(ChatColor.RED+"Too many arguments!");
			return true;
		} else {
			return true;
		}
	}
}