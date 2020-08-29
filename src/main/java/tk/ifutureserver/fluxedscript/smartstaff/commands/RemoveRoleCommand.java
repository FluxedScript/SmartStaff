package tk.ifutureserver.fluxedscript.smartstaff.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.ifutureserver.fluxedscript.smartstaff.Main;

public class RemoveRoleCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;

	public RemoveRoleCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("removerole").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a role name!");
				return true;
			}
			if (StaffModeCommand.roles.containsKey(args[0].toLowerCase())) {
				StaffModeCommand.RemoveRole(args[0].toLowerCase(), "console");
				sender.sendMessage(ChatColor.GREEN+"Removed role!");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
						+ "/removerole <role>");
				return true;
			}
		}
		Player player = (Player) sender;
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "Please specify a role name!");
			return true;
		}
		if (StaffModeCommand.roles.containsKey(args[0].toLowerCase())) {
			String rank = StaffModeCommand.isStaff(player.getUniqueId());
			if (rank == null) {
				player.sendMessage(ChatColor.DARK_RED+"You do not have permission to execute this command!");
				return true;
			}
			if (StaffModeCommand.RemoveRole(args[1].toLowerCase(), rank) == true) { // if other role isn't allowed
				player.sendMessage(ChatColor.GREEN+"Removed role!");
				return true;
			} else {
				player.sendMessage(ChatColor.DARK_RED+"You do not have permission to execute this command!");
				return true;
			}
		} else {
			player.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
					+ "/removerole <name> <role>");
			return true;
		}
		
	}
}