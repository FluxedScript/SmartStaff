package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandInterface;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

import java.util.UUID;

public class AddStaff implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Please specify a user to add to staff!");
                return false;
            }
            if (StaffData.roles.containsKey(args[2].toLowerCase())) {
                UUID staffmember = Bukkit.getPlayerExact(args[1]).getUniqueId();
                if (staffmember == null) {
                    sender.sendMessage(ChatColor.RED+"Player does not exist!");
                    return false;
                }
                StaffData.addStaff(staffmember, args[2]);
                sender.sendMessage(ChatColor.GREEN + "Added player to staff!");
            } else {
                sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
                        + "/staffmode addstaff <name> <role>");
            }
            return false;
        }
        Player player = (Player) sender;
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Please specify a user to add to staff!");
            return false;
        }
        if (!(StaffData.roles.containsKey(args[2].toLowerCase()))) {
            player.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
                    + "/staffmode addstaff <name> <role>");
            return false;
        }
        UUID staffmember = Bukkit.getPlayerExact(args[1]).getUniqueId();
        if (staffmember == null) {
            player.sendMessage(ChatColor.RED+"Player does not exist!");
            return false;
        }
        String rank = StaffData.isStaff(player.getUniqueId());
        if (rank == null) {
            player.sendMessage("You do not have permission to execute this command!");
            return false;
        }
        if (StaffData.CanManage(rank.toLowerCase(), args[2].toLowerCase())) { // if other role isn't allowed
            StaffData.addStaff(staffmember, args[2]);
            player.sendMessage(ChatColor.GREEN + "Added player to staff!");
        } else {
            player.sendMessage("You do not have permission to execute this command!");
        }
        return false;
    }
}
