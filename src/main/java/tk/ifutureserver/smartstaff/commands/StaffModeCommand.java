package tk.ifutureserver.smartstaff.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.milkbowl.vault.permission.Permission;
import tk.ifutureserver.smartstaff.Main;

public class StaffModeCommand implements CommandExecutor {
	static HashMap<UUID, ArrayList<String>> userranks = new HashMap<UUID, ArrayList<String>>(); // PLAYER UUID | RP
																								// roles
	static ArrayList<UUID> activeusers = new ArrayList<UUID>(); // Array of uuid's who are currently in staff mode //Add
																// to local/read all

	static HashMap<UUID, String> usersroles = new HashMap<UUID, String>(); // Player UUID | Staff role //Add else/read
																			// else/local
	static ArrayList<UUID> allowedusers = new ArrayList<UUID>(); // Can easily remove/add people with a command //Add
																	// else/read local
	public static HashMap<String, ArrayList<String>> roles = new HashMap<String, ArrayList<String>>(); // Role name list
																										// of roles they
																										// can manage or permission
	static HashMap<String, String> rolearray = new HashMap<String, String>(); //Rank and pex rank
	Main plugin;

	public static Logger log = Logger.getLogger("Minecraft");
	static Permission permissions = Main.getPermissions();
	String defaultrank = Main.getDefaultRank();
	List<String> ranks;

	public StaffModeCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("staffmode").setExecutor(this);

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) { // If user is not a player stop it from happening.
			sender.sendMessage("You must be a player to use this!");
			return true;
		}
		String FinalRank = null; // Initialise the staff rank variable as null
		Player player = (Player) sender; // Get player from sender to send messages
		String[] groups = permissions.getPlayerGroups(player);// gets the groups the player is in
		if (allowedusers.contains(player.getUniqueId())) { // If allowed to run command and in the allowed users hashmap
			ArrayList<String> values = new ArrayList<String>(); // local storage for groups that are removed
			if (!(activeusers.contains(player.getUniqueId()))) { // if user is not currently in staffmode
				FinalRank = rolearray.get(usersroles.get(player.getUniqueId())); //Get role from uuid and get pex group from role.
				for (String s : groups) {
					permissions.playerRemoveGroup(null, player, s);
					values.add(s);
				}
				permissions.playerAddGroup(null, player, FinalRank);
				userranks.put(player.getUniqueId(), values); // puts the rp roles into memory for later
				activeusers.add(player.getUniqueId()); // adds the player into the currently active staff members
				// save the group to a list with the player uuid then the group they got removed
				// from which would be an rp group
				Bukkit.getServer().broadcastMessage(
						ChatColor.translateAlternateColorCodes('&', player.getName() + " &cis now in &4&lSTAFF MODE"));
				return true;// broadcasts to the server
			} else if (activeusers.contains(player.getUniqueId())) { // if player is in staff mode
				for (String s : groups) {
					permissions.playerRemoveGroup(null, player, s);
				}
				player.setGameMode(GameMode.SURVIVAL);
				player.getEquipment().clear();
				player.getInventory().clear();
				for (Entry<UUID, ArrayList<String>> entry : userranks.entrySet()) { // get the group they had before
																					// they went into staff
					if (entry.getKey().equals(player.getUniqueId())) { // if player id == hashmap id
						values = entry.getValue();
						ArrayList<String> newvalues = values;
						int count = 0;
						for (String usergroupi : newvalues) {
							if (usergroupi.equalsIgnoreCase(defaultrank)) { // Checks if first value is default
								;
							} else {
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
				Bukkit.getServer().broadcastMessage(
						ChatColor.translateAlternateColorCodes('&', player.getName() + " &cis now in &b&lRP MODE"));
				return true;
			}
		}
		return true;
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
			System.out.printf("Serialized HashMap data is saved in staffmodedata.ser");
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
			System.out.printf("Serialized HashMap data is saved in staffmodedata.ser");
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
			System.out.printf("Serialized HashMap data is saved in staffmodedata.ser");
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
			System.out.printf("Serialized HashMap data is saved in staffmodedata.ser");
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
			System.out.printf("Serialized HashMap data is saved in staffmodedata.ser");
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
			System.out.printf("Serialized HashMap data is saved in staffmodedata.ser");
		} catch (IOException ioe) {
			System.out.print(
					"Could not find file. If you deleted the file or plugin folder don't worry. Just your staff ranks will be deleted. If you haven't done anything then don't worry");
		}
	}

	@SuppressWarnings("unchecked")
	public static void LoadData(File folder) {
		System.out.print(folder.toPath().toString());
		System.out.print(folder.toString()+ File.separator+ "staffmodedata.ser");
		try {
			FileInputStream fis = new FileInputStream(
					new File(folder.toString()+ File.separator, "staffmodedata.ser"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			userranks = (HashMap<UUID, ArrayList<String>>) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			System.out.print("StaffMode - On load data file not found.");
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
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
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
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
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
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
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
		}
		
		try {
			FileInputStream fis = new FileInputStream(
					new File(folder.toString()+ File.separator, "roleperms.ser"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			roles = (HashMap<String, ArrayList<String>>) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			System.out.print("StaffMode - On load data file not found.");
			ArrayList<String> perms= new ArrayList<String>();
			perms.add("ADMINISTRATOR");
			perms.add("MANAGE_ROLES");
			perms.add("MANAGE_MEMBERS");
			roles.put("console", perms); //If no role found
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			ArrayList<String> perms= new ArrayList<String>();
			perms.add("ADMINISTRATOR");
			perms.add("MANAGE_ROLES");
			perms.add("MANAGE_MEMBERS");
			roles.put("console", perms); //If no role found
			return;
		}
		
		try {
			FileInputStream fis = new FileInputStream(
					new File(folder.toString()+ File.separator, "pexroles.ser"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			rolearray = (HashMap<String, String>) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			System.out.print("StaffMode - On load data file not found.");
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
		}
	}

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

	public static void removeStaff(UUID idkey) { // Deletes staff from allowed
		allowedusers.remove(idkey); // Removes from allowed
		usersroles.remove(idkey); // Disables account roles so actions can't be performed
	}

	public static String isStaff(UUID idkey) {
		if (allowedusers.contains(idkey)) {
			return usersroles.get(idkey); // Returns the role
		} else {
			return null;
		}
	}
	public static boolean MakeRole(String Rank,String senderrank) {
		if(!(roles.get(senderrank).contains("MANAGE_ROLES") || roles.get(senderrank).contains("ADMINISTRATOR") || roles.get(senderrank).contains("MANGE_MEMBERS"))) {
			return false; //So if they don't have the permissions to do it
		}
		ArrayList<String> values = new ArrayList<String>();
		
		roles.put(Rank.toLowerCase(), values);
		for (Entry<String, ArrayList<String>> entry : roles.entrySet()) {
			ArrayList<String> temproles = new ArrayList<String>();
		    String rolename = entry.getKey();
		    if (rolename.equalsIgnoreCase(senderrank)) { //add the new role to the senders rank to manage it
		    	for(String s: entry.getValue()) {
			    	temproles.add(s);
			    }
			    temproles.add(Rank.toLowerCase());
			    roles.remove(rolename);
			    roles.put(rolename, temproles);
			    return true;
		    }
		}
		return false;
	}
	public static boolean RemoveRole(String Rank,String senderrank) {
		if(!(roles.get(senderrank).contains("MANAGE_ROLES") || roles.get(senderrank).contains("ADMINISTRATOR") || roles.get(senderrank).contains("MANGE_MEMBERS"))) {
			return false; //So if they don't have the permissiosn to do it
		}
		roles.remove(Rank.toLowerCase());
		return true;
	}
	public static boolean CanManage(String Rank, String Rank2) {
		return roles.get(Rank).contains(Rank2) && roles.get(Rank).contains("MANAGE_ROLES") || roles.get(Rank).contains("ADMINISTRATOR")  ;
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
				permissions.playerRemoveGroup(null, player, group);
			}

			permissions.playerAddGroup(null, player, rolearray.get(role));
		}
		return "Successfully updated member!";

	}

	public static HashMap<String,String> getAllowedStaff() {
		HashMap<String, String> users = new HashMap<String, String>();
		for (UUID u: allowedusers) {
			String name = Bukkit.getOfflinePlayer(u).getName();
			String rank = usersroles.get(u);
			users.put(name, rank);
		}
		return users;
	}
	
	public static ArrayList<UUID> getStaff() {
		return allowedusers;
	}
}
