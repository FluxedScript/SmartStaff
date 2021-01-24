package tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import tk.ifutureserver.fluxedscript.smartstaff.Main;

import java.io.*;
import java.util.*;

public class StaffData {
    public static HashMap<UUID, ArrayList<String>> userranks = new HashMap<>(); // PLAYER UUID | RP
    // roles
    public static ArrayList<UUID> activeusers = new ArrayList<>(); // Array of uuid's who are currently in staff mode //Add
    // to local/read all

    public static HashMap<UUID, String> usersroles = new HashMap<>(); // Player UUID | Staff role //Add else/read
    // else/local
    public static ArrayList<UUID> allowedusers = new ArrayList<>(); // Can easily remove/add people with a command //Add
    // else/read local
    public static HashMap<String, ArrayList<String>> roles = new HashMap<>(); // Role name list
    // of roles they
    // can manage or permission
    public static HashMap<String, String> rolearray = new HashMap<>(); //Rank and pex rank

    static ArrayList<UUID> needsfiring = new ArrayList<>(); //people that need to be put in gms and inv reset False if out of staffmode on firing True if needs to be reset.

    static Permission permissions = Main.getPermissions();
    static String defaultrank = Main.getDefaultRank();
    List<String> ranks;

    public static boolean addStaff(UUID idkey, String role) { // Adds staff to allowed
        if (allowedusers.contains(idkey)) { // If the staff member already exists
            return false;
        }
        if (usersroles.containsKey(idkey)) {
            return false;
        } else {
            allowedusers.add(idkey); // adds the user uuid to allow them to use the staffmode
            usersroles.put(idkey, role.toLowerCase()); // Adds the user uuid and user role to cache
            return true;
        }
    }
    public static boolean addPerm(String role, String senderrank,String perm) { // Adds staff to allowed
        if(!(roles.get(senderrank).contains("MANAGE_ROLES") || roles.get(senderrank).contains("ADMINISTRATOR"))) {
            return false; //So if they don't have the permissions to do it
        }
        ArrayList<String> perms= roles.get(role); // Perms console has always
        perms.add(perm.toUpperCase());
        roles.remove(role.toLowerCase());
        roles.put(role.toLowerCase(), perms); //If no role found
        return true;

    }
    public static boolean removePerm(String role, String senderrank, String perm) { // Adds staff to allowed
        if(!(roles.get(senderrank).contains("MANAGE_ROLES") || roles.get(senderrank).contains("ADMINISTRATOR"))) {
            return false; //So if they don't have the permissions to do it
        }
        ArrayList<String> perms= roles.get(role); // Perms console has always
        perms.remove(perm.toUpperCase());
        roles.remove(role.toLowerCase());
        roles.put(role.toLowerCase(), perms); //If no role found
        return true;

    }
    public static Boolean befired(UUID idkey) {
        if (needsfiring.contains(idkey)) {
            needsfiring.remove(idkey);
            return true;
        }
        return false;
    }
    public static void removeStaff(UUID idkey) { // Deletes staff from allowed
        OfflinePlayer playeroffline = Bukkit.getOfflinePlayer(idkey);
        if (playeroffline.isOnline()) { //if player is online
            //DecimalFormat df = new DecimalFormat("0.00");
            Player player = playeroffline.getPlayer(); // Get player from sender to send messages
            String[] groups = permissions.getPlayerGroups(player);// gets the groups the player is in
            ArrayList<String> values; // local storage for groups that are removed
            if (!(activeusers.contains(player.getUniqueId()))) { // if user is not currently in staffmode
                allowedusers.remove(idkey);// Removes from allowed
                usersroles.remove(idkey); // Disables account roles so actions can't be performed
                // broadcasts to the server
            } else if (activeusers.contains(player.getUniqueId())) { // if player is in staff mode

                //IN STAFF MODE GOING TO RP MODE
                for (String s : groups) {
                    permissions.playerRemoveGroup(null, player, s);
                }
                player.setGameMode(GameMode.CREATIVE);
                player.setGameMode(GameMode.SURVIVAL);
                for (PotionEffect effect : player.getActivePotionEffects())
                    player.removePotionEffect(effect.getType());
                //Location topfloor = player.getPlayer().getWorld().getHighestBlockAt(player.getPlayer().getLocation().getBlockX(), player.getPlayer().getLocation().getBlockZ()).getLocation();
                //Location currentloc = player.getLocation();
                //Location oldloc = player.getLocation().subtract(0, Float.valueOf(df.format(currentloc.distance(topfloor)))-2.0, 0);
                //player.teleport(oldloc);
                player.getEquipment().clear();
                player.getInventory().clear();

                for (Map.Entry<UUID, ArrayList<String>> entry : userranks.entrySet()) { // get the group they had before
                    // they went into staff
                    if (entry.getKey().equals(player.getUniqueId())) { // if player id == hashmap id

                        values = entry.getValue();
                        ArrayList<String> newvalues = values;
                        int count = 0;
                        for (String usergroupi : newvalues) {
                            if (!(usergroupi.equalsIgnoreCase(defaultrank))) { // Checks if first value is default
                                count += 1;
                                permissions.playerAddGroup(null, player, usergroupi); // adds each rp role back but
                                // avoids the default unless no
                                // roles are there
                            }
                        }
                        if (count == 0) {// if no other roles are found
                            permissions.playerAddGroup(null, player, defaultrank); // Adds the default role
                        }
                        break; // Found
                    }
                }

                userranks.remove(player.getUniqueId()); // Remove rp role from storage to save on ram usage
                activeusers.remove(player.getUniqueId()); // Remove from online users
                allowedusers.remove(idkey);// Removes from allowed
                usersroles.remove(idkey); // Disables account roles so actions can't be performed
                activeusers.remove(idkey);
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getName() + " &cis now in &b&lRP MODE"));

            }
        } else { //player is not online
            // Disables account roles so actions can't be performed
            // Removes from allowed
            // Remove from online users
            // Remove rp role from storage to save on ram usage
            if (activeusers.contains(idkey)){  // in staffmode
                needsfiring.add(idkey);
                OfflinePlayer player = Bukkit.getOfflinePlayer(idkey);
                String[] groups = permissions.getPlayerGroups(null, player);
                ArrayList<String> values; // local storage for groups that are removed
                for (String s : groups) {
                    permissions.playerRemoveGroup(null, player, s);
                }

                for (Map.Entry<UUID, ArrayList<String>> entry : userranks.entrySet()) { // get the group they had before
                    // they went into staff
                    if (entry.getKey().equals(player.getUniqueId())) { // if player id == hashmap id
                        values = entry.getValue();
                        ArrayList<String> newvalues = values;
                        int count = 0;
                        for (String usergroupi : newvalues) {
                            if (!(usergroupi.equalsIgnoreCase(defaultrank))) { // Checks if first value is default
                                count += 1;
                                permissions.playerAddGroup(null, player, usergroupi); // adds each rp role back but
                                // avoids the default unless no
                                // roles are there
                            }
                        }
                        if (count == 0) {// if no other roles are found
                            permissions.playerAddGroup(null, player, defaultrank); // Adds the default role
                        }
                        break; // Found
                    }
                }

            }
            userranks.remove(idkey); // Remove rp role from storage to save on ram usage
            activeusers.remove(idkey); // Remove from online users
            allowedusers.remove(idkey);// Removes from allowed
            usersroles.remove(idkey); // Disables account roles so actions can't be performed
            activeusers.remove(idkey);
        }

    }

    public static String isStaff(UUID idkey) {
        if (allowedusers.contains(idkey)) {
            return usersroles.get(idkey); // Returns the role
        } else {
            return null;
        }
    }
    public static boolean MakeRole(String Rank,String senderrank, String pexrank) {
        if(!(roles.get(senderrank).contains("MANAGE_ROLES") || roles.get(senderrank).contains("ADMINISTRATOR") || roles.get(senderrank).contains("MANGE_MEMBERS"))) {
            return false; //So if they don't have the permissions to do it
        }
        ArrayList<String> values = new ArrayList<>();

        roles.put(Rank.toLowerCase(), values);
        for(String correct_group : permissions.getGroups()) {
            if (correct_group.equalsIgnoreCase(pexrank)) {
                rolearray.put(Rank.toLowerCase(), correct_group);
            }
        }
        return true;
    }
    public static boolean RemoveRole(String Rank,String senderrank) {
        if(!(roles.get(senderrank).contains("MANAGE_ROLES") || roles.get(senderrank).contains("ADMINISTRATOR") || roles.get(senderrank).contains("MANGE_MEMBERS"))) {
            return false; //So if they don't have the permissions to do it
        }
        roles.remove(Rank.toLowerCase());
        rolearray.remove(Rank.toLowerCase());
        for (UUID value : allowedusers) {
            String rank = usersroles.get(value);
            if (rank.equals(Rank.toLowerCase())) {
                usersroles.remove(value);
                allowedusers.remove(value);
                activeusers.remove(value);
                OfflinePlayer player = Bukkit.getOfflinePlayer(value);
                String[] groups = permissions.getPlayerGroups(null, player);
                for (String s : groups) {
                    permissions.playerRemoveGroup(null, player, s);
                }
                ArrayList<String> values;
                for (Map.Entry<UUID, ArrayList<String>> entry : userranks.entrySet()) { // get the group they had before
                    // they went into staff
                    if (entry.getKey().equals(player.getUniqueId())) { // if player id == hashmap id
                        values = entry.getValue();
                        ArrayList<String> newvalues = values;
                        int count = 0;
                        for (String usergroupi : newvalues) {
                            if (!(usergroupi.equalsIgnoreCase(defaultrank))) { // Checks if first value is default
                                count += 1;
                                permissions.playerAddGroup(null, player, usergroupi); // adds each rp role back but
                                // avoids the default unless no
                                // roles are there
                            }
                        }
                        if (count == 0) {// if no other roles are found
                            permissions.playerAddGroup(null, player, defaultrank); // Adds the default role
                        }
                        break; // Found
                    }
                }
            }
        }
        return true;
    }
    public static boolean CanManage(String Rank, String s) {
        return roles.get(Rank).contains("MANAGE_ROLES") || roles.get(Rank).contains("ADMINISTRATOR")  ;
    }

    public static String UpdateStaff(UUID idkey, String role) { // Adds staff to allowed
        if (!(allowedusers.contains(idkey))) { // If the staff member already exists
            return "That player isn't staff!";
        }
        usersroles.remove(idkey);
        usersroles.put(idkey, role);
        if (activeusers.contains(idkey)) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(idkey);
            for (String group : permissions.getPlayerGroups(null, player)) {
                if (!(permissions.playerRemoveGroup(null, player, group))){
                     for(String correct_group : permissions.getGroups()){
                         if (correct_group.equalsIgnoreCase(group)){
                             permissions.playerRemoveGroup(null, player, group);
                         }
                     }
                }
            }

            permissions.playerAddGroup(null, player, rolearray.get(role));
        }
        return "Successfully updated member!";

    }

    public static HashMap<String,String> getAllowedStaff() {
        HashMap<String, String> users = new HashMap<>();
        for (UUID u: allowedusers) {
            String name = Bukkit.getOfflinePlayer(u).getName();
            String rank = usersroles.get(u);
            users.put(name, rank);
        }
        return users;
    }
    public static ArrayList<String> getAllRanks() {
        ArrayList<String> values = new ArrayList<>();
        values.add(ChatColor.GOLD+"System rank : Permissions group");
        for (Map.Entry<String, String> entry : rolearray.entrySet()) {
            String rolename = entry.getKey();
            String pexrank = entry.getValue();
            ArrayList<String> temproles = roles.get(rolename);
            if (temproles.contains("ADMINISTRATOR")) {
                values.add(ChatColor.DARK_RED+""+rolename + " : "+pexrank);
            } else if(temproles.contains("MANGE_MEMBERS")) {
                values.add(ChatColor.BLUE+""+rolename + " : "+pexrank);
            } else if(temproles.contains("MANGE_ROLES")) {
                values.add(ChatColor.BLUE+""+rolename + " : "+pexrank);
            }else {
                values.add(ChatColor.GRAY+""+rolename + " : "+pexrank);
            }
        }
        if (values.isEmpty()) {
            return null;
        }
        return values;

    }
    public static ArrayList<UUID> getStaff() {

        return allowedusers;
    }

    public static void SaveData(File folder) {
        // FileConfiguration data = YamlConfiguration.loadConfiguration(new
        // File(plugin.getDataFolder(), "detectors.yml"));

        try {//userranks
            FileOutputStream fos = new FileOutputStream(
                    new File(folder.toString()+ File.separator, "staffmodedata.ser"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(userranks);
            oos.close();
            fos.close();
            System.out.print("Serialized HashMap data is saved in staffmodedata.ser");
        } catch (IOException ioe) {
            System.out.print(
                    "Could not find file. If you deleted the file or plugin folder don't worry. Just your staff ranks will be deleted. If you haven't done anything then don't worry");
        }

        try {//activeusers
            FileOutputStream fos = new FileOutputStream(
                    new File(folder.toString()+ File.separator, "activeusers.ser"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(activeusers);
            oos.close();
            fos.close();
            System.out.print("Serialized HashMap data is saved in staffmodedata.ser");
        } catch (IOException ioe) {
            System.out.print(
                    "Could not find file. If you deleted the file or plugin folder don't worry. Just your staff ranks will be deleted. If you haven't done anything then don't worry");
        }

        try {//usersroles
            FileOutputStream fos = new FileOutputStream(
                    new File(folder.toString()+ File.separator, "usersroles.ser"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(usersroles);
            oos.close();
            fos.close();
            System.out.print("Serialized HashMap data is saved in staffmodedata.ser");
        } catch (IOException ioe) {
            System.out.print(
                    "Could not find file. If you deleted the file or plugin folder don't worry. Just your staff ranks will be deleted. If you haven't done anything then don't worry");
        }

        try {//allowedusers
            FileOutputStream fos = new FileOutputStream(
                    new File(folder.toString()+ File.separator, "allowedstaff.ser"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allowedusers);
            oos.close();
            fos.close();
            System.out.print("Serialized HashMap data is saved in staffmodedata.ser");
        } catch (IOException ioe) {
            System.out.print(
                    "Could not find file. If you deleted the file or plugin folder don't worry. Just your staff ranks will be deleted. If you haven't done anything then don't worry");
        }
        try {//roles
            FileOutputStream fos = new FileOutputStream(
                    new File(folder.toString()+ File.separator, "roleperms.ser"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(roles);
            oos.close();
            fos.close();
            System.out.print("Serialized HashMap data is saved in staffmodedata.ser");
        } catch (IOException ioe) {
            System.out.print(
                    "Could not find file. If you deleted the file or plugin folder don't worry. Just your staff ranks will be deleted. If you haven't done anything then don't worry");
        }
        try {//rolearray
            FileOutputStream fos = new FileOutputStream(
                    new File(folder.toString()+ File.separator, "pexroles.ser"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(rolearray);
            oos.close();
            fos.close();
            System.out.print("Serialized HashMap data is saved in staffmodedata.ser");
        } catch (IOException ioe) {
            System.out.print(
                    "Could not find file. If you deleted the file or plugin folder don't worry. Just your staff ranks will be deleted. If you haven't done anything then don't worry");
        }
    }

    @SuppressWarnings("unchecked")
    public static void LoadData(@org.jetbrains.annotations.NotNull File folder) {
        try {
            FileInputStream fis = new FileInputStream(
                    new File(folder.toString()+ File.separator, "staffmodedata.ser"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            userranks = (HashMap<UUID, ArrayList<String>>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            System.out.print("StaffMode - On load data file not found.");
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
        try {
            FileInputStream fis = new FileInputStream(
                    new File(folder.toString()+ File.separator, "activeusers.ser"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            activeusers = (ArrayList<UUID>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            System.out.print("StaffMode - On load data file not found.");
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }

        try {
            FileInputStream fis = new FileInputStream(
                    new File(folder.toString()+ File.separator, "usersroles.ser"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            usersroles = (HashMap<UUID, String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            System.out.print("StaffMode - On load data file not found.");
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }

        try {
            FileInputStream fis = new FileInputStream(
                    new File(folder.toString()+ File.separator, "allowedstaff.ser"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            allowedusers = (ArrayList<UUID>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            System.out.print("StaffMode - On load data file not found.");
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }

        try {
            FileInputStream fis = new FileInputStream(
                    new File(folder.toString()+ File.separator, "roleperms.ser"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            roles = (HashMap<String, ArrayList<String>>) ois.readObject();
            ois.close();
            fis.close();
            if(roles.isEmpty()) {
                ArrayList<String> perms= new ArrayList<>(); // Perms console has always
                perms.add("ADMINISTRATOR");
                perms.add("MANAGE_ROLES");
                perms.add("MANAGE_MEMBERS");
                roles.put("console", perms); //If no role found
            }
        } catch (IOException ioe) {
            System.out.print("StaffMode - On load data file not found.");
            ArrayList<String> perms= new ArrayList<>();
            perms.add("ADMINISTRATOR");
            perms.add("MANAGE_ROLES");
            perms.add("MANAGE_MEMBERS");
            roles.put("console", perms); //If no role found

        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
            ArrayList<String> perms= new ArrayList<>();
            perms.add("ADMINISTRATOR");
            perms.add("MANAGE_ROLES");
            perms.add("MANAGE_MEMBERS");
            roles.put("console", perms); //If no role found
        }

        try {
            FileInputStream fis = new FileInputStream(
                    new File(folder.toString()+ File.separator, "pexroles.ser"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            rolearray = (HashMap<String, String>) ois.readObject();
            ois.close();
            fis.close();
            if (rolearray.isEmpty()) {
                rolearray.put("console", "CONSOLE"); //If no role found
            }
        } catch (IOException ioe) {
            System.out.print("StaffMode - On load data file not found.");
            rolearray.put("console", "CONSOLE"); //If no role found
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            rolearray.put("console", "CONSOLE"); //If no role found
            c.printStackTrace();
        }
    }
}
