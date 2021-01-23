package tk.ifutureserver.fluxedscript.smartstaff.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LangSupport {
    public static String getStaffModeEnterMsg(){
        File LangFile = new File (Bukkit.getServer().getPluginManager().getPlugin("SmartStaff").getDataFolder(), "messages.yml");
        FileConfiguration LangMsgs = YamlConfiguration.loadConfiguration(LangFile);
        return (String) LangMsgs.get("StaffMode");
    }
    public static String getStaffModeLeaveMsg(){
        File LangFile = new File (Bukkit.getServer().getPluginManager().getPlugin("SmartStaff").getDataFolder(), "messages.yml");
        FileConfiguration LangMsgs = YamlConfiguration.loadConfiguration(LangFile);
        return (String) LangMsgs.get("RpMode");
    }
    public static String getStaffModePrefix(){
        File LangFile = new File (Bukkit.getServer().getPluginManager().getPlugin("SmartStaff").getDataFolder(), "messages.yml");
        FileConfiguration LangMsgs = YamlConfiguration.loadConfiguration(LangFile);
        return (String) LangMsgs.get("prefix");
    }
}
