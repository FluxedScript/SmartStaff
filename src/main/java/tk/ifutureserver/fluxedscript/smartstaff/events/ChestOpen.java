package tk.ifutureserver.fluxedscript.smartstaff.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;

import java.util.HashMap;
import java.util.UUID;


public class ChestOpen implements Listener {
    public static HashMap<UUID, Inventory> openInventories = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(!(StaffData.isCurrentStaff(player.getUniqueId()))) return;
        if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
        if (!(block.getState() instanceof Container)) return;

        event.setCancelled(true);

        Container cont = (Container) block.getState();
        Inventory inventory_items = cont.getInventory();
        int size = inventory_items.getSize();
        String title = inventory_items.getTitle();


        Inventory inv = Bukkit.createInventory(null, size, title);
        ItemStack[] contents = inventory_items.getContents();
        inv.setContents(contents);

        openInventories.put(player.getUniqueId(), inv);

        player.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {

        if (openInventories.get(event.getWhoClicked().getUniqueId()) == null) return;

        event.setCancelled(true);

        final ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player player = (Player) event.getWhoClicked();

    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent event) {
        if(!(StaffData.isCurrentStaff(event.getWhoClicked().getUniqueId()))) return;
        if (openInventories.get(event.getWhoClicked().getUniqueId()) == null) return;
        event.setCancelled(true);
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryOpenEvent event) {
        if(!(StaffData.isCurrentStaff(event.getPlayer().getUniqueId()))) return;
        if (event.getInventory().getType() == InventoryType.PLAYER) return;
        openInventories.put(event.getPlayer().getUniqueId(), event.getInventory());

    }


    // Remove from array
    @EventHandler
    public void onInventoryClick(final InventoryCloseEvent event) {
        if(!(StaffData.isCurrentStaff(event.getPlayer().getUniqueId()))) return;
        if (openInventories.get(event.getPlayer().getUniqueId()) == null) return;
        openInventories.remove(event.getPlayer().getUniqueId());
    }

}
