package tk.ifutureserver.smartstaff.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.ifutureserver.smartstaff.Main;

public class RemoveStaffCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;

	public RemoveStaffCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("removestaff").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			System.out.print("Executes");
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a user to remove from staff!");
				return true;
			}
			UUID staffmember = Bukkit.getPlayerExact(args[0]).getUniqueId();
			if (staffmember == null) {
				sender.sendMessage("Player not found!");
				sender.sendMessage(ChatColor.GREEN + "Removed player from staff!");
				return true;
			}
			StaffModeCommand.removeStaff(staffmember);
			return true;
		}
		System.out.print("Executes2");
		Player player = (Player) sender;
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "Please specify a user to remove from staff!");
			return true;
		}
		UUID staffmember = Bukkit.getPlayerExact(args[0]).getUniqueId();
		if (staffmember == null) {
			sender.sendMessage("Player not found!");
			return true;
		}
		String rank = StaffModeCommand.isStaff(player.getUniqueId());
		if (rank == null) {
			player.sendMessage("You do not have permission to execute this command!");
			return true;
		}
		if (StaffModeCommand.CanManage(rank.toLowerCase(), args[1].toLowerCase())) { // if other role isn't allowed
			StaffModeCommand.removeStaff(staffmember);
			player.sendMessage(ChatColor.GREEN + "Removed player from staff!");
			return true;
		} else {
			player.sendMessage("You do not have permission to execute this command!");
			return true;
		}
	}
}
