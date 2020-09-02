package tk.ifutureserver.fluxedscript.smartstaff.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import tk.ifutureserver.fluxedscript.smartstaff.Main;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
public class TabComplete implements TabCompleter{
	Main plugin;
    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("removestaff")){
            if(sender instanceof Player){
                List<String> list = new ArrayList<>();
                for (UUID u : StaffData.getStaff()) {
                	OfflinePlayer player= Bukkit.getOfflinePlayer(u);
                	list.add(player.getName());
                }
                return list;
            }
        }
        return null;
    }
}