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
                "\n" + ChatColor.GRAY + "/staffmode createrole <Group Name> <Permissions group>" +
                "\n" + ChatColor.GRAY + "/staffmode deleterole <Group Name>" +
                "\n" + ChatColor.GRAY + "/staffmode updaterole <Player Name> <Group Name>" +
                "\n" + ChatColor.GRAY + "/staffmode viewroles" +
                "\n" + ChatColor.GRAY + "/staffmode addperm <Group Name> <perm>" +
                "\n" + ChatColor.GRAY + "/staffmode removeperm <Group Name> <perm>" +
                "\n" + ChatColor.GRAY + "/staffmode addstaff <Player Name> <Group Name>"+
                "\n" + ChatColor.GRAY + "/staffmode removestaff <Player Name> <Group Name>"+
                "\n" + ChatColor.GRAY + "/staffmode stafflist";
        if (!(sender instanceof Player)) {
            sender.sendMessage(help_msg);
            return true;
        }
        Player player = (Player) sender;
        player.sendMessage(help_msg);
        return true;
    }
}
