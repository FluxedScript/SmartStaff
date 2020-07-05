package tk.ifutureserver.smartstaff.commands;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import tk.ifutureserver.smartstaff.Main;
public class HelloCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;
	
	public HelloCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("bbtest").setExecutor(this);
	}
	public static ItemStack createSkull(String url) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);//
        headMeta.setDisplayName("BlockSensor");
        headMeta.setLore(Arrays.asList("Detects a block break", "Level: "+ "1", ChatColor.translateAlternateColorCodes('&', "&0") + UUID.randomUUID().toString()));
        profile.getProperties().put("textures", new Property("textures", url));
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        }catch (IllegalArgumentException error) {
            error.printStackTrace();
        }catch(NoSuchFieldException e) {
        	e.printStackTrace();
        }catch(SecurityException e) {
        	e.printStackTrace();
        }catch(IllegalAccessException e) {
        	e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players may execute this command!");
			return true;
		}
		Player player = (Player) sender;
		if (player.hasPermission("bb.test")) {
			player.sendMessage("Perms are loaded!");
			return true;
		} else {
			player.sendMessage("You do not have permission to execute this command!");
		}
		return false;
	}
}
