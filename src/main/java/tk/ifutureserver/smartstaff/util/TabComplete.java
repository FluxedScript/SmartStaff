package tk.ifutureserver.smartstaff.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import tk.ifutureserver.smartstaff.Main;
import tk.ifutureserver.smartstaff.commands.StaffModeCommand;
public class TabComplete implements TabCompleter{
	Main plugin;
    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("removestaff") && args.length >= 0){
            if(sender instanceof Player){
                List<String> list = new ArrayList<String>();
                for (UUID u : StaffModeCommand.getStaff()) {
                	OfflinePlayer player= Bukkit.getOfflinePlayer(u);
                	list.add(player.getName());
                }
                return list;
            }
        }
        return null;
    }
}