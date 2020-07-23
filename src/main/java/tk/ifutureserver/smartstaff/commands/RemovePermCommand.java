package tk.ifutureserver.smartstaff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.ifutureserver.smartstaff.Main;

public class RemovePermCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;
	
	public RemovePermCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("permremove").setExecutor(this);
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a role to remove a perm from!");
				return true;
			}else if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Please specify a perm for the role! \nValid perms:"+ChatColor.GOLD+" administrator,manage_roles,manage_members");
				return true;
			}
			if (StaffModeCommand.roles.containsKey(args[0].toLowerCase())) { //if there is a role
				StaffModeCommand.removePerm(args[0].toLowerCase(),"console", args[1]);
				sender.sendMessage(ChatColor.GREEN + "Removed "+args[1]+" from "+args[0]);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
						+ "/permremove <role> <perm>");
				return true;
			}
			
		}
		Player player = (Player) sender;
		String rank = StaffModeCommand.isStaff(player.getUniqueId());
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Please specify a role to remove a perm from!");
			return true;
		}else if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Please specify a perm for the role! \nValid perms:"+ChatColor.GOLD+" administrator,manage_roles,manage_members");
			return true;
		}
		if (StaffModeCommand.roles.containsKey(args[0].toLowerCase())) { //if there is a role
			Boolean success = StaffModeCommand.removePerm(args[0].toLowerCase(),rank, args[1]);
			if (success) {
				sender.sendMessage(ChatColor.GREEN + "Removed "+args[1]+" from "+args[0]);
			} else {
				sender.sendMessage(ChatColor.DARK_RED+"You do not have permission to remove perms from roles!");
			}
			
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
					+ "/permremove <role> <perm>");
			return true;
		}		
	}
}
