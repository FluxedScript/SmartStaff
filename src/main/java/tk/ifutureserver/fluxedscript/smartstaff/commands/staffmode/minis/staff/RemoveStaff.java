package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandInterface;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

import java.util.UUID;

public class RemoveStaff implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Please specify a user to remove from staff!");
                return true;
            }
            @SuppressWarnings("deprecation")
            UUID staffmember = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
            if (staffmember == null) {
                sender.sendMessage("Player not found!");
                sender.sendMessage(ChatColor.GREEN + "Removed player from staff!");
                return true;
            }
            StaffData.removeStaff(staffmember);
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Please specify a user to remove from staff!");
            return true;
        }
        UUID staffmember = Bukkit.getPlayerExact(args[1]).getUniqueId();
        if (staffmember == null) {
            sender.sendMessage("Player not found!");
            return true;
        }
        String rank = StaffData.isStaff(player.getUniqueId());
        if (rank == null) {
            player.sendMessage("You do not have permission to execute this command!");
            return true;
        }
        if (StaffData.CanManage(rank.toLowerCase(), args[2].toLowerCase())) { // if other role isn't allowed
            StaffData.removeStaff(staffmember);
            player.sendMessage(ChatColor.GREEN + "Removed player from staff!");
        } else {
            player.sendMessage("You do not have permission to execute this command!");
        }
        return true;
    }
}
