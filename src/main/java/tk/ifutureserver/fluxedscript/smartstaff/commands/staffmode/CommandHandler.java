package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandHandler implements CommandExecutor
{

    //This is where we will store the commands
    private static final HashMap<String, CommandInterface> commands = new HashMap<>();

    //Register method.
    public void register(String name, CommandInterface cmd) {

        //Put command into hashmap
        commands.put(name, cmd);
    }

    public boolean exists(String name) {

        //Return hashmap
        return commands.containsKey(name);
    }

    //Getter method for the Executor.
    public CommandInterface getExecutor(String name) {
        //Returns a command in the hashmap of the same name.
        return commands.get(name);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(args.length == 0) {
            getExecutor("staffmode").onCommand(sender, cmd, commandLabel, args);
            return true;
        }
        //if there is an arguments
        if(exists(args[0])){
            getExecutor(args[0]).onCommand(sender, cmd, commandLabel, args);
        } else {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
        }
        return true;
    }

}