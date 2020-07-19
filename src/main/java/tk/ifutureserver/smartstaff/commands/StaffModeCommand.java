package tk.ifutureserver.smartstaff.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

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
	static String defaultrank = Main.getDefaultRank();
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
		DecimalFormat df = new DecimalFormat("0.00");
		String FinalRank = null; // Initialise the staff rank variable as null
		Player player = (Player) sender; // Get player from sender to send messages
		String[] groups = permissions.getPlayerGroups(player);// gets the groups the player is in
		ArrayList<String> values = new ArrayList<String>(); // local storage for groups that are removed
		
		if (allowedusers.contains(player.getUniqueId())) { // If allowed to run command and in the allowed users hashmap
			
			// if player is in allowedusers
			
			if (!(activeusers.contains(player.getUniqueId()))) { // if user is not currently in staffmode
				
				// ALREADY IN RP GOING INTO STAFFMODE
				
				FinalRank = rolearray.get(usersroles.get(player.getUniqueId())); //Get role from uuid and get pex group from role.
				for (String s : groups) {
					System.out.print(s);
					permissions.playerRemoveGroup(null, player, s);
					values.add(s);
				}
				System.out.print(FinalRank);
				permissions.playerAddGroup(null, player, FinalRank);
				userranks.put(player.getUniqueId(), values); // puts the rp roles into memory for later
				activeusers.add(player.getUniqueId()); // adds the player into the currently active staff members
				// save the group to a list with the player uuid then the group they got removed
				// from which would be an rp group
				Bukkit.getServer().broadcastMessage(
						ChatColor.translateAlternateColorCodes('&', player.getName() + " &cis now in &4&lSTAFF MODE"));
				return true;// broadcasts to the server
			} else if (activeusers.contains(player.getUniqueId())) { // if player is in staff mode
				
				//IN STAFF MODE GOING TO RP MODE
				for (String s : groups) {
					System.out.print("Removed "+s);
					permissions.playerRemoveGroup(null, player, s);
				}
				System.out.print(values);
				player.setGameMode(GameMode.CREATIVE);
				player.setGameMode(GameMode.SURVIVAL);
				for (PotionEffect effect : player.getActivePotionEffects())
			        player.removePotionEffect(effect.getType());
				Location topfloor = player.getPlayer().getWorld().getHighestBlockAt(player.getPlayer().getLocation().getBlockX(), player.getPlayer().getLocation().getBlockZ()).getLocation();
				Location currentloc = player.getLocation();
				Location oldloc = player.getLocation().subtract(0, Float.valueOf(df.format(currentloc.distance(topfloor)))-1.0, 0);
				player.teleport(oldloc);
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
				Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getName() + " &cis now in &b&lRP MODE"));
				return true;
				
			}
		}else {
			player.sendMessage(ChatColor.DARK_RED+"You do not have permission to execute this command!");
			return true;
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
				ArrayList<String> perms= new ArrayList<String>(); // Perms console has always
				perms.add("ADMINISTRATOR");
				perms.add("MANAGE_ROLES");
				perms.add("MANAGE_MEMBERS");
				roles.put("console", perms); //If no role found
			}
		} catch (IOException ioe) {
			System.out.print("StaffMode - On load data file not found.");
			ArrayList<String> perms= new ArrayList<String>();
			perms.add("ADMINISTRATOR");
			perms.add("MANAGE_ROLES");
			perms.add("MANAGE_MEMBERS");
			roles.put("console", perms); //If no role found
			System.out.print("Roles:"+roles);

		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			ArrayList<String> perms= new ArrayList<String>();
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
		//allowedusers.remove(idkey);// Removes from allowed
		//usersroles.remove(idkey); // Disables account roles so actions can't be performed
		//activeusers.remove(idkey);
		OfflinePlayer playeroffline = Bukkit.getOfflinePlayer(idkey);
		if (playeroffline.isOnline() == true) { //if player is online
			DecimalFormat df = new DecimalFormat("0.00");
			String FinalRank = null; // Initialise the staff rank variable as null
			Player player = playeroffline.getPlayer(); // Get player from sender to send messages
			String[] groups = permissions.getPlayerGroups(player);// gets the groups the player is in
			ArrayList<String> values = new ArrayList<String>(); // local storage for groups that are removed
			if (!(activeusers.contains(player.getUniqueId()))) { // if user is not currently in staffmode
				
				// ALREADY IN RP GOING INTO STAFFMODE
				
				FinalRank = rolearray.get(usersroles.get(player.getUniqueId())); //Get role from uuid and get pex group from role.
				for (String s : groups) {
					System.out.print(s);
					permissions.playerRemoveGroup(null, player, s);
					values.add(s);
				}
				System.out.print(FinalRank);
				permissions.playerAddGroup(null, player, FinalRank);
				userranks.put(player.getUniqueId(), values); // puts the rp roles into memory for later
				activeusers.add(player.getUniqueId()); // adds the player into the currently active staff members
				// save the group to a list with the player uuid then the group they got removed
				// from which would be an rp group
				Bukkit.getServer().broadcastMessage(
						ChatColor.translateAlternateColorCodes('&', player.getName() + " &cis now in &4&lSTAFF MODE"));
				return;// broadcasts to the server
			} else if (activeusers.contains(player.getUniqueId())) { // if player is in staff mode
				
				//IN STAFF MODE GOING TO RP MODE
				for (String s : groups) {
					System.out.print("Removed "+s);
					permissions.playerRemoveGroup(null, player, s);
				}
				System.out.print(values);
				player.setGameMode(GameMode.CREATIVE);
				player.setGameMode(GameMode.SURVIVAL);
				for (PotionEffect effect : player.getActivePotionEffects())
			        player.removePotionEffect(effect.getType());
				Location topfloor = player.getPlayer().getWorld().getHighestBlockAt(player.getPlayer().getLocation().getBlockX(), player.getPlayer().getLocation().getBlockZ()).getLocation();
				Location currentloc = player.getLocation();
				Location oldloc = player.getLocation().subtract(0, Float.valueOf(df.format(currentloc.distance(topfloor)))-1.0, 0);
				player.teleport(oldloc);
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
				allowedusers.remove(idkey);// Removes from allowed
				usersroles.remove(idkey); // Disables account roles so actions can't be performed
				activeusers.remove(idkey);
				Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getName() + " &cis now in &b&lRP MODE"));
				return;
				
			}
		} else {
			//add to a list to check on join
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
		System.out.print(roles);
		if(!(roles.get(senderrank).contains("MANAGE_ROLES") || roles.get(senderrank).contains("ADMINISTRATOR") || roles.get(senderrank).contains("MANGE_MEMBERS"))) {
			return false; //So if they don't have the permissions to do it
		}
		ArrayList<String> values = new ArrayList<String>();
		
		roles.put(Rank.toLowerCase(), values);
		rolearray.put(Rank.toLowerCase(), pexrank);
		System.out.print(roles);
		System.out.print(rolearray);
		return true;
	}
	public static boolean RemoveRole(String Rank,String senderrank) {
		if(!(roles.get(senderrank).contains("MANAGE_ROLES") || roles.get(senderrank).contains("ADMINISTRATOR") || roles.get(senderrank).contains("MANGE_MEMBERS"))) {
			return false; //So if they don't have the permissions to do it
		}
		roles.remove(Rank.toLowerCase());
		rolearray.remove(Rank.toLowerCase());
		Iterator<UUID> it = allowedusers.iterator();
		while (it.hasNext()) {
			UUID value = it.next();
			String rank = usersroles.get(value);
			if (rank == Rank.toLowerCase()) {
				usersroles.remove(value);
				System.out.print(allowedusers);
				int value2 = allowedusers.indexOf(value);
				allowedusers.remove(value2);
				System.out.print(allowedusers);
				activeusers.remove(value);
				OfflinePlayer player = Bukkit.getOfflinePlayer(value);
				String[] groups = permissions.getPlayerGroups(null, player);
				for (String s : groups) {
					permissions.playerRemoveGroup(null, player, s);
				}
				ArrayList<String> values = new ArrayList<String>();
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
			} 
		}
		return true;
	}
	public static boolean CanManage(String Rank, String Rank2) {
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
	public static ArrayList<String> getAllRanks() {
		ArrayList<String> values = new ArrayList<String>();
		System.out.print("executes!");
		System.out.print(rolearray);
		for (Entry<String, String> entry : rolearray.entrySet()) {
		    String rolename = entry.getKey();
		    String pexrank = entry.getValue();
		    System.out.print(rolename);
		    System.out.print(pexrank);
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
		    System.out.print(values);
		}
		System.out.print(values);
		if (values.isEmpty()) {
			return null;
		}
		return values;
		
	}
	public static ArrayList<UUID> getStaff() {
		return allowedusers;
	}
}
