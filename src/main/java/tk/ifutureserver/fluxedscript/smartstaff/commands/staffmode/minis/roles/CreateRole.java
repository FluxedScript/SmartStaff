package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.roles;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandInterface;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

public class CreateRole implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "Please specify a role name!");
                return true;
            }
            if (args.length == 2) {
                sender.sendMessage(ChatColor.RED + "Please specify a pex group!");
                return true;
            }
            if (args.length > 3) {
                sender.sendMessage(ChatColor.RED + "Too many arugments!");
                return true;
            }
            if (StaffData.roles.containsKey(args[1].toLowerCase())) {
                sender.sendMessage("This role already exists!");
                return true;
            } else {
                StaffData.MakeRole(args[1], "console", args[2]);
                sender.sendMessage(ChatColor.RED + "Added role! ");
                return true;
            }
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "Please specify a role name!");
            return true;
        }
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "Please specify a pex group!");
            return true;
        }
        if (args.length > 3) {
            sender.sendMessage(ChatColor.RED + "Too many arugments!");
            return true;
        }
        if (StaffData.roles.containsKey(args[2].toLowerCase())) {
            player.sendMessage(ChatColor.RED + "This role already exists! ");
        }  else {
            StaffData.MakeRole(args[1], StaffData.isStaff(player.getUniqueId()), args[2]);
            player.sendMessage(ChatColor.RED + "Added role! ");
            System.out.print(StaffData.userranks);
            return true;
        }
        return true;
    }
}
