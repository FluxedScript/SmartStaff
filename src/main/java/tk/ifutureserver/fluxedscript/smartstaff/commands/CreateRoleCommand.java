package tk.ifutureserver.fluxedscript.smartstaff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.ifutureserver.fluxedscript.smartstaff.Main;

public class CreateRoleCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;

	public CreateRoleCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("addrole").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a role name!");
				return true;
			}
			if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a pex group!");
				return true;
			}
			if (args.length > 2) {
				sender.sendMessage(ChatColor.RED + "Too many arugments!");
				return true;
			}
			if (StaffModeCommand.roles.containsKey(args[1].toLowerCase())) {
				sender.sendMessage("This role already exists!");
				return true;
			} else {
				StaffModeCommand.MakeRole(args[0], "console", args[1]);
				sender.sendMessage(ChatColor.RED + "Added role! ");
				return true;
			}
		}
		Player player = (Player) sender;
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please specify a role name!");
			return true;
		}
		if (args.length == 1) {
			sender.sendMessage(ChatColor.RED + "Please specify a pex group!");
			return true;
		}
		if (args.length > 2) {
			sender.sendMessage(ChatColor.RED + "Too many arugments!");
			return true;
		}
		if (StaffModeCommand.roles.containsKey(args[1].toLowerCase())) {
			player.sendMessage(ChatColor.RED + "This role already exists! ");
		}  else {
			StaffModeCommand.MakeRole(args[0], StaffModeCommand.isStaff(player.getUniqueId()), args[1]);
			player.sendMessage(ChatColor.RED + "Added role! ");
			return true;
		}
		return true;
		
	}
}