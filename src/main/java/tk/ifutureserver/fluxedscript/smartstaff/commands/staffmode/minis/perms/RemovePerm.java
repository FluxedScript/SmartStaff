package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.perms;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandInterface;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

public class RemovePerm implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Please specify a role to remove a perm from!");
                return true;
            }else if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Please specify a perm for the role! \nValid perms:"+ChatColor.GOLD+" administrator,manage_roles,manage_members");
                return true;
            }
            if (StaffData.roles.containsKey(args[1].toLowerCase())) { //if there is a role
                StaffData.removePerm(args[1].toLowerCase(),"console", args[2]);
                sender.sendMessage(ChatColor.GREEN + "Removed "+args[2]+" from "+args[1]);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
                        + "/staffmode removeperm <role> <perm>");
                return true;
            }

        }
        Player player = (Player) sender;
        String rank = StaffData.isStaff(player.getUniqueId());
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Please specify a role to remove a perm from!");
            return true;
        }else if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Please specify a perm for the role! \nValid perms:"+ChatColor.GOLD+" administrator,manage_roles,manage_members");
            return true;
        }
        if (StaffData.roles.containsKey(args[1].toLowerCase())) { //if there is a role
            boolean success = StaffData.removePerm(args[1].toLowerCase(),rank, args[2]);
            if (success) {
                sender.sendMessage(ChatColor.GREEN + "Removed "+args[2]+" from "+args[1]);
            } else {
                sender.sendMessage(ChatColor.DARK_RED+"You do not have permission to remove perms from roles!");
            }

            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Please specify a correct role name! " + ChatColor.GOLD
                    + "/staffmode removeperm <role> <perm>");
            return true;
        }
    }
}
