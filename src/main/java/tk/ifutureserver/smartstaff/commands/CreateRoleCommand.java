package tk.ifutureserver.smartstaff.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.ifutureserver.smartstaff.Main;

public class CreateRoleCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;

	public CreateRoleCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("addrole").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a role name!");
				return true;
			}
			if (args.length < 2) {
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
				sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
						+ "/addrole <role> <pexgroup>");
				return true;
			}
		}
		Player player = (Player) sender;
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "Please specify a user to add to staff!");
			return true;
		}
		if (StaffModeCommand.roles.containsKey(args[1].toLowerCase())) {
			;
		} else {
			player.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
					+ "/smartstaff addstaff <name> <role>");
			return true;
		}
		UUID staffmember = Bukkit.getPlayerExact(args[0]).getUniqueId();
		if (staffmember == null) {
			player.sendMessage(ChatColor.RED+"Player does not exist!");
			return true;
		}
		String rank = StaffModeCommand.isStaff(player.getUniqueId());
		if (rank == null) {
			player.sendMessage("You do not have permission to execute this command!");
			return true;
		}
		if (StaffModeCommand.CanManage(rank.toLowerCase(), args[1].toLowerCase())) { // if other role isn't allowed
			StaffModeCommand.addStaff(staffmember, args[1]);
			player.sendMessage(ChatColor.GREEN + "Added player to staff!");
			return true;
		} else {
			player.sendMessage("You do not have permission to execute this command!");
			return true;
		}
	}
}