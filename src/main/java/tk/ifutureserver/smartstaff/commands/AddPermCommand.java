package tk.ifutureserver.smartstaff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.ifutureserver.smartstaff.Main;

public class AddPermCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;
	
	public AddPermCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("permadd").setExecutor(this);
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a role to add a perm to!");
				return true;
			}else if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Please specify a perm for the role! \nValid perms:"+ChatColor.GOLD+" administrator,manage_roles,manage_members");
				return true;
			}
			if (StaffModeCommand.roles.containsKey(args[0].toLowerCase())) { //if there is a role
				StaffModeCommand.addPerm(args[0].toLowerCase(),"console", args[1]);
				sender.sendMessage(ChatColor.GREEN + "Added "+args[1]+" to "+args[0]);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
						+ "/permadd <role> <perm>");
				return true;
			}
			
		}
		Player player = (Player) sender;
		String rank = StaffModeCommand.isStaff(player.getUniqueId());
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Please specify a role to add a perm to!");
			return true;
		}else if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Please specify a perm for the role! \nValid perms:"+ChatColor.GOLD+" administrator,manage_roles,manage_members");
			return true;
		}
		if (StaffModeCommand.roles.containsKey(args[0].toLowerCase())) { //if there is a role
			Boolean success = StaffModeCommand.addPerm(args[0].toLowerCase(),rank, args[1]);
			if (success) {
				sender.sendMessage(ChatColor.GREEN + "Added "+args[1]+" to "+args[0]);
			} else {
				sender.sendMessage(ChatColor.DARK_RED+"You do not have permission to add roles to users!");
			}
			
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
					+ "/permadd <role> <perm>");
			return true;
		}		
	}
}
