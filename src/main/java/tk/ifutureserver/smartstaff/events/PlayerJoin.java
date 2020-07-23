package tk.ifutureserver.smartstaff.events;

import java.text.DecimalFormat;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

import tk.ifutureserver.smartstaff.Main;
import tk.ifutureserver.smartstaff.commands.StaffModeCommand;

public class PlayerJoin implements Listener {
	@SuppressWarnings("unused")
	private Main plugin;
	@EventHandler
	private void playerJoin(PlayerJoinEvent e) {
		if (StaffModeCommand.befired(e.getPlayer().getUniqueId())) {
			DecimalFormat df = new DecimalFormat("0.00");
			Player player = e.getPlayer();
			player.setGameMode(GameMode.CREATIVE);
			player.setGameMode(GameMode.SURVIVAL);
			for (PotionEffect effect : player.getActivePotionEffects())
		        player.removePotionEffect(effect.getType());
			Location topfloor = player.getPlayer().getWorld().getHighestBlockAt(player.getPlayer().getLocation().getBlockX(), player.getPlayer().getLocation().getBlockZ()).getLocation();
			Location currentloc = player.getLocation();
			Location oldloc = player.getLocation().subtract(0, Float.valueOf(df.format(currentloc.distance(topfloor)))-2.0, 0);
			player.teleport(oldloc);
			player.getEquipment().clear();
			player.getInventory().clear();
		}
	}
}