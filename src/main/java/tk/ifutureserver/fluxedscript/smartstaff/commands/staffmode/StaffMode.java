package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import tk.ifutureserver.fluxedscript.smartstaff.Main;
import tk.ifutureserver.fluxedscript.smartstaff.util.LangSupport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class StaffMode implements CommandInterface {
    Main plugin;
    static Permission permissions = Main.getPermissions();
    static String defaultrank = Main.getDefaultRank();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) { // If user is not a player stop it from happening.
            sender.sendMessage("You must be a player to use this!");
            return true;
        }
        //DecimalFormat df = new DecimalFormat("0.00");
        String FinalRank; // Initialise the staff rank variable as null
        Player player = (Player) sender; // Get player from a sender to send messages
        String[] groups = permissions.getPlayerGroups(player);// gets the groups the player is in
        ArrayList<String> values = new ArrayList<>(); // local storage for groups that are removed

        if (StaffData.allowedusers.contains(player.getUniqueId())) { // If allowed to run command and in the allowed users hashmap

            // if player is in allowedusers

            if (!(StaffData.activeusers.contains(player.getUniqueId()))) { // if user is not currently in staffmode

                // ALREADY IN RP GOING INTO STAFF MODE

                FinalRank = StaffData.rolearray.get(StaffData.usersroles.get(player.getUniqueId())); //Get role from uuid and get pex group from role.
                for (String s : groups) {
                    permissions.playerRemoveGroup(null, player, s);
                    values.add(s);
                }
                permissions.playerAddGroup(null, player, FinalRank);
                StaffData.userranks.put(player.getUniqueId(), values); // puts the rp roles into memory for later
                StaffData.activeusers.add(player.getUniqueId()); // adds the player into the currently active staff members
                // save the group to a list with the player uuid then the group they got removed
                // from which would be an rp group
                Bukkit.getServer().broadcastMessage(
                        ChatColor.translateAlternateColorCodes('&', LangSupport.getStaffModeEnterMsg().replace("%player%",player.getName())));
                return false;// broadcasts to the server
            } else if (StaffData.activeusers.contains(player.getUniqueId())) { // if player is in staff mode

                //IN STAFF MODE GOING TO RP MODE
                for (String s : groups) {
                    permissions.playerRemoveGroup(null, player, s);
                }

                System.out.print(values);
                player.setGameMode(GameMode.CREATIVE);
                player.setGameMode(GameMode.SURVIVAL);
                for (PotionEffect effect : player.getActivePotionEffects())
                    player.removePotionEffect(effect.getType());

                player.getEquipment().clear();
                player.getInventory().clear();

                for (Map.Entry<UUID, ArrayList<String>> entry : StaffData.userranks.entrySet()) { // get the group they had before
                    // they went into staff
                    if (entry.getKey().equals(player.getUniqueId())) { // if player id == hashmap id

                        values = entry.getValue();
                        ArrayList<String> newvalues = values;
                        int count = 0;
                        for (String usergroupi : newvalues) {

                            if (!(usergroupi.equalsIgnoreCase(defaultrank))) { // Checks if first value is default
                                if (!(StaffData.rolearray.containsValue(usergroupi)) && !(StaffData.rolearray.containsValue(usergroupi.toLowerCase()))){
                                    count += 1;
                                    permissions.playerAddGroup(null, player, usergroupi); // adds each rp role back but
                                    // avoids the default unless no
                                    // roles are there
                                    // avoids staff roles in case of a misuse of command
                                }

                            }
                        }
                        if (count == 0) {// if no other roles are found
                            permissions.playerAddGroup(null, player, defaultrank); // Adds the default role
                        }
                        break; // Found
                    }
                }
                StaffData.userranks.remove(player.getUniqueId()); // Remove rp role from storage to save on ram usage
                StaffData.activeusers.remove(player.getUniqueId()); // Remove from online users
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', LangSupport.getStaffModeLeaveMsg().replace("%player%",player.getName())));
                return false;

            }
        }else {
            player.sendMessage(ChatColor.DARK_RED+"You do not have permission to execute this command!");
            return false;
        }

        return false;
    }
}
