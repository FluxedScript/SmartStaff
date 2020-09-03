package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandInterface;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;
import tk.ifutureserver.fluxedscript.smartstaff.util.Numbers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class getStaff implements CommandInterface {
    public ArrayList<String> getStaff2(int pagenum) {
        ArrayList<String> leaderboard = new ArrayList<>();
        HashMap<String, String> tempusers = StaffData.getAllowedStaff();
        int pages = 1;
        int records = 0;
        for ( Map.Entry<String, String> ignored : tempusers.entrySet()) {
            records++;
            if ((records % 12) == 0) {
                pages++;
            }
        }
        final String e = ChatColor.YELLOW + "------ " + ChatColor.GOLD + "Staff Members" + ChatColor.YELLOW + " -- "
                + ChatColor.GOLD + "Page " + ChatColor.RED + pagenum + ChatColor.GOLD + "/" + ChatColor.RED + pages
                + ChatColor.GOLD + "----";
        leaderboard.add(e);
        int count = 0;
        for (Map.Entry<String, String> entry : tempusers.entrySet()) {
            count++;
            String name = entry.getKey();
            String rank = entry.getValue();
            leaderboard.add(ChatColor.GREEN + "" + name + " => " + rank);
            if (count == (12 * pagenum)) {
                leaderboard.add(ChatColor.GOLD + "Type " + ChatColor.RED + "/staffmode stafflist " + (pagenum + 1) + ChatColor.GOLD
                        + " to read the next page!");
                break;
            } else if (count == (12 * (pagenum - 1))) {
                leaderboard.removeAll(leaderboard);
                leaderboard.add(e);
            }
        }
        if (count == 0){
            leaderboard.removeAll(leaderboard);
            leaderboard.add(ChatColor.DARK_GRAY+"No staff onboard this ship!");
        }
        return leaderboard;

    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            int num = Integer.parseInt(args[1]);
            if (args[1].isEmpty()){
                num = 1;
            }
            sender.sendMessage(String.join("\n", getStaff2(num)));
            return false;
        }
        Player player = (Player) sender;
        if (player.hasPermission("smartstaff.viewstaff")) {
            player.sendMessage(String.join("\n", getStaff2(1)));
            return false;
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
        }
        return false;

    }
}
