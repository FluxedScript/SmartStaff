package tk.ifutureserver.fluxedscript.smartstaff.events;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import tk.ifutureserver.fluxedscript.smartstaff.Main;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

public class PlayerInteract implements Listener {

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (StaffData.isCurrentStaff(player.getUniqueId())) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You can't drop the item in staff mode! Your request has been logged!");
			Location block = player.getLocation();
			System.out.print(ChatColor.translateAlternateColorCodes('&',"&4"+player.getName()+" &ltried to drop an item &rat location "+block.getX()+" , "+block.getY()+" , "+block.getZ() + " This item was "+event.getItemDrop().getItemStack().getType()));
			Player result = null;
			double distance = 0;
			double lastDistance = Double.MAX_VALUE;
			for(Player p : player.getWorld().getPlayers()) {
			    if(player == p)
			        continue;
			 
			    distance = player.getLocation().distance(p.getLocation());
			    if(distance < lastDistance) {
			        lastDistance = distance;
			        result = p;
			    }
			}
			 
			if(result != null) {
				System.out.print("The closest player was "+ result.getName() + "at the distance of "+distance+" blocks");
				
			} else {
			    System.out.print("No players were on the server!");
			    result = player;
			    System.out.print("The closest player was "+ result.getName());
			}
			for (Player a : Bukkit.getOnlinePlayers()) {
				if (a.getGameMode() == GameMode.CREATIVE && a.hasPermission("ss.use") && a.getUniqueId() != player.getUniqueId()) {
					a.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4"+player.getName()+" &ltried to drop an item &rat location "+block.getX()+" , "+block.getY()+" , "+block.getZ() + " This item was "+event.getItemDrop().getItemStack().getType()));;
					a.sendMessage("The closest player was "+result.getName()+" at a distance of "+distance+" blocks");
				}
			}
			
			try{
				File logfile = new File(Main.fetchDataFolder(),"loggedactions.txt");
				if(logfile.exists()) {
					;
				} else {
					logfile.createNewFile();
				}
				FileWriter fr = new FileWriter(logfile, true);
				BufferedWriter br = new BufferedWriter(fr);
				OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
				br.write(player.getName()+" tried to drop an item."+ " This item was "+event.getItemDrop().getItemStack().getType()+"The closest player was "+ result.getName() + "at the distance of "+distance+" blocks "+"at location "+block.getX()+" , "+block.getY()+" , "+block.getZ() + " At day: "+utc.getDayOfMonth()+"/"+utc.getMonth()+"/"+utc.getYear()+" at time: "+utc.getHour()+": "+utc.getMinute()+":"+utc.getSecond()+" UTC time\n");
				br.close();
				fr.close();
			} catch (IOException e) {
			   System.out.print("Could not log event to server!");
			}
		}else {
			return;
		}
	}
}
