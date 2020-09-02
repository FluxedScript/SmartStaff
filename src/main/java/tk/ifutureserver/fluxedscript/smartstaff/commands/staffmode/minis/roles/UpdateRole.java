package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.roles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandInterface;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

import java.util.UUID;

public class UpdateRole implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Please specify a user to update their role!");
                return true;
            }
            if (!(StaffData.roles.containsKey(args[2].toLowerCase()))) {
                sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
                        + "/updatestaff <name> <role>");
                return true;
            }
            UUID staffmember = Bukkit.getPlayerExact(args[1]).getUniqueId();
            if (staffmember == null) {
                sender.sendMessage("Player not found!");
                return true;
            }
            StaffData.UpdateStaff(staffmember, args[2]);
            sender.sendMessage(ChatColor.GREEN + "Updated staff member!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Please specify a user to update their role!");
            return true;
        }
        if (StaffData.roles.containsKey(args[2].toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
                    + "/updatestaff <name> <role>");
        }
        UUID staffmember = Bukkit.getPlayerExact(args[1]).getUniqueId();
        String rank = StaffData.isStaff(player.getUniqueId());
        if (rank == null) {
            player.sendMessage("You do not have permission to execute this command!");
            return true;
        }
        if (StaffData.CanManage(rank.toLowerCase(), args[2].toLowerCase())) { // if other role isn't allowed
            String rank3 = StaffData.isStaff(staffmember);
            if (rank3 != null && StaffData.CanManage(rank.toLowerCase(), rank3.toLowerCase())) {
                StaffData.UpdateStaff(staffmember, args[2]);
                player.sendMessage(ChatColor.GREEN + "Updated staff member!");
                return true;
            }
        }
        player.sendMessage("You do not have permission to execute this command!");
        return true;
    }
}
