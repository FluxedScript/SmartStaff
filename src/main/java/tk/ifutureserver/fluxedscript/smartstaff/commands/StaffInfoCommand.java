package tk.ifutureserver.fluxedscript.smartstaff.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.ifutureserver.fluxedscript.smartstaff.Main;

public class StaffInfoCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;

	public StaffInfoCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("stafflist").setExecutor(this);
	}
	public ArrayList<String> getStaff(int pagenum) {
		ArrayList<String> leaderboard = new ArrayList<String>();
		HashMap<String, String> tempusers = StaffModeCommand.getAllowedStaff();
		int pages = 1;
		int records = 0;
		for (@SuppressWarnings("unused") Entry<String, String> s : tempusers.entrySet()) {
			records++;
			if ((records % 12) == 0) {
				pages++;
			}
		}
		leaderboard.add(ChatColor.YELLOW + "------ " + ChatColor.GOLD + "Staff Members" + ChatColor.YELLOW + " -- "
				+ ChatColor.GOLD + "Page " + ChatColor.RED + pagenum + ChatColor.GOLD + "/" + ChatColor.RED + pages
				+ ChatColor.GOLD + "----");
		int count = 0;
		for (Entry<String, String> entry : tempusers.entrySet()) {
			count++;
			String name = entry.getKey();
			String rank = entry.getValue();
			leaderboard.add(ChatColor.GREEN + "" + name + " => " + rank);
			if (count == (12 * pagenum)) {
				break;
			} else if (count == (12 * (pagenum - 1))) {
				leaderboard.removeAll(leaderboard);
				leaderboard.add(ChatColor.YELLOW + "------ " + ChatColor.GOLD + "Staff Members" + ChatColor.YELLOW
						+ " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + pagenum + ChatColor.GOLD + "/"
						+ ChatColor.RED + pages + ChatColor.GOLD + "----");
			}
		}
		leaderboard.add(ChatColor.GOLD + "Type " + ChatColor.RED + "/stafflist " + (pagenum + 1) + ChatColor.GOLD
				+ " to read the next page!");
		return leaderboard;
		
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(String.join("\n", getStaff(1)));
			return true;
		}
		Player player = (Player) sender;
		if (player.hasPermission("smartstaff.viewstaff")) {
			player.sendMessage(String.join("\n", getStaff(1)));
			return true;
		} else {
			player.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
		}
		return false;
	}
}