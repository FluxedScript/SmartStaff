package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.roles;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandInterface;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

import java.util.Objects;

public class ViewRole implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(String.join("\n", Objects.requireNonNull(StaffData.getAllRanks())));
        } else if (args.length >2) {
            sender.sendMessage(ChatColor.RED+"Too many arguments!");
        }
        return true;
    }
}
