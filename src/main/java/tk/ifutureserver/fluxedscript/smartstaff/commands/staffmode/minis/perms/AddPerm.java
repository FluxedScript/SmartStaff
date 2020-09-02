package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.perms;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandInterface;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

public class AddPerm implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Please specify a role to add a perm to!");
                return false;
            }else if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Please specify a perm for the role! \nValid perms:"+ChatColor.GOLD+" administrator,manage_roles,manage_members");
                return false;
            }
            if (StaffData.roles.containsKey(args[1].toLowerCase())) { //if there is a role
                StaffData.addPerm(args[1].toLowerCase(),"console", args[2]);
                sender.sendMessage(ChatColor.GREEN + "Added "+args[2]+" to "+args[1]);
            } else {
                sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
                        + "/smartstaff addperm <role> <perm>");
            }
            return false;

        }
        Player player = (Player) sender;
        String rank = StaffData.isStaff(player.getUniqueId());
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Please specify a role to add a perm to!");
            return true;
        }else if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Please specify a perm for the role! \nValid perms:"+ChatColor.GOLD+" administrator,manage_roles,manage_members");
            return true;
        }
        if (StaffData.roles.containsKey(args[1].toLowerCase())) { //if there is a role
            boolean success = StaffData.addPerm(args[1].toLowerCase(),rank, args[2]);
            if (success) {
                sender.sendMessage(ChatColor.GREEN + "Added "+args[2]+" to "+args[1]);
            } else {
                sender.sendMessage(ChatColor.DARK_RED+"You do not have permission to add roles to users!");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
                    + "/staffmode addperm <role> <perm>");
        }
        return true;
    }
}
