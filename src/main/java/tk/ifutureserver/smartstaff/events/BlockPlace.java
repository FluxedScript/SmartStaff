package tk.ifutureserver.smartstaff.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import tk.ifutureserver.smartstaff.Main;

public class BlockPlace implements Listener {
	@SuppressWarnings("unused")
	private Main plugin;
	@EventHandler
	private void onBlockPlace(BlockPlaceEvent e) {
		System.out.print("hello");
	}
}
