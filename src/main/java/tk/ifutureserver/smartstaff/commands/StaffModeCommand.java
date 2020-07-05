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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.milkbowl.vault.permission.Permission;
import tk.ifutureserver.smartstaff.Main;

public class StaffModeCommand implements CommandExecutor {
	static HashMap<UUID, ArrayList<String>> userranks = new HashMap<UUID, ArrayList<String>>();
	Main plugin;
	public static  Logger log = Logger.getLogger("Minecraft");
	Permission permissions = Main.getPermissions();
	List<String> ranks;
	
	public StaffModeCommand(Main plugin) {
		    this.plugin = plugin;
		    plugin.getCommand("staffmode").setExecutor(this);
		    
	 }
	@SuppressWarnings("unchecked")
	 public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		    if (!(sender instanceof Player)) {
		    	sender.sendMessage("You must be a player to use this!");
			    return true;
		    }
		    ranks = (List<String>) plugin.getConfig().get("ranks");
		    String FinalRank = null;
		    Player player = (Player)sender;
		    if (player.hasPermission("SS.use")) {
		    	
		    	ArrayList<String> values = new ArrayList<String>();
		    	String group = permissions.getPrimaryGroup(player.getWorld().getName(), player);
		    	if (player.getGameMode().equals(GameMode.SURVIVAL)) {
		    		if(player.hasPermission("ss.realadmin") || player.hasPermission("*") || player.isOp()) {
			    		System.out.print(player.getName()+" is an op trying to use staffmode!");
			    		log.warning("SmartStaff relies on the user having ss.use perms and an ss.rank where rank is what rank they are. Mod is ss.mod and director is ss.direct");
			    		player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4&lPlease do not use this command when you have * perms or are opped.&r Command blocked."));
			    		return false;
			    	}
			    	if(player.hasPermission("ss.trialmod")) {
			    		  FinalRank = "TrialMod";
			    	} else if(player.hasPermission("ss.mod")) {
			    		  FinalRank = "Mod";
			    	} else if(player.hasPermission("ss.admin")) {
			    		  FinalRank = "Admin";
			    	} else if(player.hasPermission("ss.sradmin")) {
			    		  FinalRank = "SrAdmin";
			    	} else if(player.hasPermission("ss.dev")) {
			    		  FinalRank = "Dev";
			    	} else if(player.hasPermission("ss.executive")) {
			    		  FinalRank = "Executive";
			    	} else if(player.hasPermission("ss.servermanager")) {
			    		  FinalRank = "ServerManager";
			    	} else if(player.hasPermission("ss.direct")) {
			    		  FinalRank = "Direct";
			    	} else if(player.hasPermission("ss.owner")) {
			    		  FinalRank = "Owner";
			    	} else {
			    		  player.sendMessage("You do not have the permission to do this!");
				    	return true;
			    	}
		    		player.setGameMode(GameMode.CREATIVE);
		    		 //gets the group
		    		String[] groups = permissions.getPlayerGroups(player);
		    		for (String s: groups) {
			    		permissions.playerRemoveGroup(null, player, s);
		    		    values.add(s);
		    		}
		    		permissions.playerAddGroup(null, player, FinalRank);
		    		//permissions.playerAddGroup(null, player, "Citizen");
		    		userranks.put(player.getUniqueId(), values);
		    		//save the group to a list with the player uuid then the group they got removed from which would be an rp group
		    		Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',player.getName() + " &cis now in &4&lSTAFF MODE"));
			        return true;
			    } else if (player.getGameMode().equals(GameMode.CREATIVE)) {
			    	player.setGameMode(GameMode.SURVIVAL);
			    	player.getEquipment().clear();
			    	player.getInventory().clear();
			    	permissions.playerRemoveGroup(null,player, group);
			    	//permissions.playerRemoveGroup(null, player, "Citizen");
			    	for (Entry<UUID, ArrayList<String>> entry : userranks.entrySet()) {
						if (entry.getKey().equals(player.getUniqueId())) { //if player id == hashmap id
							values = entry.getValue();
							ArrayList<String> newvalues = values;
							int count = 0;
							for (String usergroupi: newvalues) {
								if (usergroupi.equalsIgnoreCase("citizen")) {
									;
								}else {
									count += 1;
									permissions.playerAddGroup(null, player, usergroupi);
								}
							}
							if (count == 0) {
								permissions.playerAddGroup(null, player, "Citizen");
							}
							break;
						}
					}
			    	userranks.remove(player.getUniqueId());
			    	Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',player.getName() + " &cis now in &b&lRP MODE"));
			    	//get the group they had before they went into staff
			    	return true;
			    } 
		    } 
		    return false;
	  }
	public static void SaveData(File folder) {
		//FileConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "detectors.yml"));
		try{
            FileOutputStream fos  =new FileOutputStream("staffmodedata.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(userranks);
            oos.close();
            fos.close();
            System.out.printf("Serialized HashMap data is saved in staffmodedata.ser");
        }catch(IOException ioe){
            System.out.print("Could not find file. If you deleted the file or plugin folder don't worry. Just your staff ranks will be deleted. If you haven't done anything then don't worry");
        }		 
	}
	@SuppressWarnings("unchecked")
	public static void LoadData(File folder) {
		try
	      {
	         FileInputStream fis = new FileInputStream("staffmodedata.ser");
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         userranks = (HashMap<UUID, ArrayList<String>>) ois.readObject();
	         ois.close();
	         fis.close();
	      }catch(IOException ioe)
	      {
	         System.out.print("StaffMode - On load data file not found.");
	         return;
	      }catch(ClassNotFoundException c)
	      {
	         System.out.println("Class not found");
	         c.printStackTrace();
	         return;
	      }
	      System.out.println("Deserialized HashMap..");
	      System.out.print(userranks);
	}
}
