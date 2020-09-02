package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.roles;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandInterface;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

public class DeleteRole implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Please specify a role name!");
                return true;
            }
            if (StaffData.roles.containsKey(args[1].toLowerCase())) {
                StaffData.RemoveRole(args[1].toLowerCase(), "console");
                sender.sendMessage(ChatColor.GREEN+"Removed role!");
            } else {
                sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
                        + "/removerole <role>");
            }
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Please specify a role name!");
            return true;
        }
        if (StaffData.roles.containsKey(args[1].toLowerCase())) {
            String rank = StaffData.isStaff(player.getUniqueId());
            if (rank == null) {
                player.sendMessage(ChatColor.DARK_RED+"You do not have permission to execute this command!");
                return true;
            }
            if (StaffData.RemoveRole(args[1].toLowerCase(), rank)) { // if other role isn't allowed
                player.sendMessage(ChatColor.GREEN+"Removed role!");
            } else {
                player.sendMessage(ChatColor.DARK_RED+"You do not have permission to execute this command!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
                    + "/staffmode deleterole <name> <role>");
        }
        return true;
    }
}
