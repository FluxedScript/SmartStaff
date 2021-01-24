package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandInterface;

public class Help implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String help_msg = ChatColor.GREEN + "Staff" + ChatColor.GOLD + "Mode" + ChatColor.RED + " Help" +
                "\n" + ChatColor.GRAY + "/staffmode createrole <Group Name> <Permissions group>" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Create a role for staff mode" +
                "\n" + ChatColor.GRAY + "/staffmode deleterole <Group Name>" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Delete a role for staff mode" +
                "\n" + ChatColor.GRAY + "/staffmode updaterole <Player Name> <Group Name>" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Change a role for a staff member promote/demote" +
                "\n" + ChatColor.GRAY + "/staffmode viewroles" + ChatColor.GOLD + " - " + ChatColor.WHITE + "View all staff mode roles" +
                "\n" + ChatColor.GRAY + "/staffmode addperm <Group Name> <perm>" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Add a pre set permission for a role (Not permission nodes)" +
                "\n" + ChatColor.GRAY + "/staffmode removeperm <Group Name> <perm>" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Remove a pre set permission for a role (Not permission nodes)" +
                "\n" + ChatColor.GRAY + "/staffmode addstaff <Player Name> <Group Name>"+ ChatColor.GOLD + " - " + ChatColor.WHITE + "Set a staff role for a player/hire them" +
                "\n" + ChatColor.GRAY + "/staffmode removestaff <Player Name> <Group Name>"+ ChatColor.GOLD + " - " + ChatColor.WHITE + "Remove a staff role for a player/fire them" +
                "\n" + ChatColor.GRAY + "/staffmode stafflist"+  ChatColor.GOLD + " - " + ChatColor.WHITE + "View all staff";
        if (!(sender instanceof Player)) {
            sender.sendMessage(help_msg);
            return true;
        }
        Player player = (Player) sender;
        player.sendMessage(help_msg);
        return true;
    }
}
