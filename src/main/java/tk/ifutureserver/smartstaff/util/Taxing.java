package tk.ifutureserver.smartstaff.util;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import tk.ifutureserver.smartstaff.Main;

public class Taxing  implements Listener {
	static Economy econ = Main.getEconomy();
	public static void taxing() {
    	
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
    	    @Override
    	    public void run() {
    	    	Calendar c = Calendar.getInstance();
    	    	int count = 0;
    	    	if (c.get(Calendar.HOUR_OF_DAY) == 0) {
    	    		for (OfflinePlayer player: Bukkit.getOfflinePlayers()) {
    	    			double balance = econ.getBalance(player);
    	    			double endbalance =   (balance*(Main.getTaxAmount()/100.0f));
    	    			EconomyResponse response = econ.withdrawPlayer(player,Math.round(endbalance));
    	    			if(response.type == EconomyResponse.ResponseType.FAILURE){
    	    				System.out.print("Error taxing "+player.getName());
    	    			} else {
    	    				count ++;
    	    			}
    	        	}
    	    		Bukkit.broadcastMessage(ChatColor.GOLD+"Taxed "+ChatColor.RED+count+" users.");
    	            
    	        }
    	    	for (OfflinePlayer player: Bukkit.getOfflinePlayers()) {
    	    		System.out.print("-----------------------");
	    			double balance = econ.getBalance(player);
	    			System.out.print(balance);
	    			double endbalance =   (balance*(Main.getTaxAmount()/100.0f));
	    			System.out.print(endbalance);
	    			EconomyResponse response = econ.withdrawPlayer(player,Math.round(endbalance));
	    			System.out.print(response.type);
	    			System.out.print("-----------------------");
	        	}
    	    }
    	}, 0L, 40L); //0 Tick initial delay, 20 Tick (1 Second) between repeats
    }
}
