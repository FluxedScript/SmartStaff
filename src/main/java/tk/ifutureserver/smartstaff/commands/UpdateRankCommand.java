package tk.ifutureserver.smartstaff.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.ifutureserver.smartstaff.Main;

public class UpdateRankCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;

	public UpdateRankCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("updatestaff").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a user to update their role!");
				return true;
			}
			if (StaffModeCommand.roles.containsKey(args[1].toLowerCase())) {
				sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
						+ "/updatestaff <name> <role>");
			}
			UUID staffmember = Bukkit.getPlayerExact(args[0]).getUniqueId();
			if (staffmember == null) {
				sender.sendMessage("Player not found!");
				return true;
			}
			StaffModeCommand.UpdateStaff(staffmember, args[1]);
			sender.sendMessage(ChatColor.GREEN + "Updated member staff!");
			return true;
		}
		Player player = (Player) sender;
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "Please specify a user to update their role!");
			return true;
		}
		if (StaffModeCommand.roles.containsKey(args[1].toLowerCase())) {
			sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
					+ "/updatestaff <name> <role>");
		}
		UUID staffmember = Bukkit.getPlayerExact(args[0]).getUniqueId();
		String rank = StaffModeCommand.isStaff(player.getUniqueId());
		if (rank == null) {
			player.sendMessage("You do not have permission to execute this command!");
			return true;
		}
		if (StaffModeCommand.CanManage(rank.toLowerCase(), args[1].toLowerCase())) { // if other role isn't allowed
			String rank3 = StaffModeCommand.isStaff(staffmember);
			if (StaffModeCommand.CanManage(rank.toLowerCase(), rank3.toLowerCase())) {
				StaffModeCommand.UpdateStaff(staffmember, args[1]);
				player.sendMessage(ChatColor.GREEN + "Updated staff member!");
				return true;
			}
			player.sendMessage("You do not have permission to execute this command!");
			return true;
		} else {
			player.sendMessage("You do not have permission to execute this command!");
			return true;
		}
	}
}