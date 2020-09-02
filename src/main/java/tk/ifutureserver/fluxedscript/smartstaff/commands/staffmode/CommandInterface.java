package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode;

import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

public interface CommandInterface {
    boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);
}