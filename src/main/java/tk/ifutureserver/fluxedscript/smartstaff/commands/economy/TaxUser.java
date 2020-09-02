package tk.ifutureserver.fluxedscript.smartstaff.commands.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tk.ifutureserver.fluxedscript.smartstaff.Main;

public class TaxUser implements CommandExecutor {
	static Economy econ = Main.getEconomy();

	public TaxUser(Main plugin) {
		plugin.getCommand("taxall").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("ss.tax")) {
			int count = 0;
			for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
				try {
					double balance = econ.getBalance(player);
					double endbalance = (balance * (Main.getTaxAmount() / 100.0f));
					EconomyResponse response = econ.withdrawPlayer(player, Math.round(endbalance));
					if (response.type == EconomyResponse.ResponseType.FAILURE) {
						System.out.print("Error taxing " + player.getName());
					} else {
						count++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			Bukkit.broadcastMessage(ChatColor.GOLD + "Taxed " + ChatColor.RED + count + " users.");
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permissions to execute this command!");
		}

		return true;
	}
}
