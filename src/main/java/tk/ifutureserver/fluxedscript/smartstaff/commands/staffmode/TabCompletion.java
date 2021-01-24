package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;

public class TabCompletion implements TabCompleter {
    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("addstaff");
        list.add("removestaff");
        list.add("liststaff");
        list.add("viewroles");
        list.add("updaterole");
        list.add("deleterole");
        list.add("createrole");
        list.add("addperm");
        list.add("delperm");
        list.add("help");

        ArrayList<String> newlist = new ArrayList<>();
        for (String scmd : list){
            if (scmd.startsWith(args[0])){
                if (scmd.equalsIgnoreCase(args[0])){
                    newlist = null;
                } else{
                    assert newlist != null;
                    newlist.add(scmd);
                }

            }
        }

        return newlist;
    }

}
