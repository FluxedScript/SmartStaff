package tk.ifutureserver.fluxedscript.smartstaff;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import tk.ifutureserver.fluxedscript.smartstaff.commands.AddPermCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.AddStaffCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.CreateRoleCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.HelloCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.PlayTimeCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.RemovePermCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.RemoveRoleCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.RemoveStaffCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.StaffInfoCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.StaffModeCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.TaxUser;
import tk.ifutureserver.fluxedscript.smartstaff.commands.UpdateRankCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.ViewRolesCommand;
import tk.ifutureserver.fluxedscript.smartstaff.events.PlayerInteract;
import tk.ifutureserver.fluxedscript.smartstaff.events.PlayerJoin;
import tk.ifutureserver.fluxedscript.smartstaff.util.Taxing;

public class Main extends JavaPlugin implements Listener {
	public static  Logger log = Logger.getLogger("Minecraft");
	private static Permission perms = null;
    private static Chat chat = null;
    private static Economy econ = null;
    private static float taxamount = 1;
    public static String oldpassword = null;
    public static String defaultrank = null;
    public static File DataFolder = null;
    HttpServer server = null;
    private static Main instance;
    public static Main getInstance(){
        return instance;
    }
	@Override
    public void onEnable() {
		instance = this;
		if (!setupPermissions() ) {
            log.severe("SmartStaff - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		setupEconomy();
		StaffModeCommand.LoadData(this.getDataFolder());
		//setupChat();
		getConfig().options().copyDefaults(true);
		saveConfig();
		taxamount = (float)getConfig().getDouble("taxes");
		defaultrank = getConfig().getString("DefaultRank");
		DataFolder = getDataFolder();
        getLogger().info("SmartStaff is online!");
        new HelloCommand(this);
        new TaxUser(this);
        new PlayTimeCommand(this);
        new StaffModeCommand(this);
        new StaffInfoCommand(this);
        new AddStaffCommand(this);
        new RemoveStaffCommand(this);
        new RemoveRoleCommand(this);
        new CreateRoleCommand(this);
        new UpdateRankCommand(this);
        new ViewRolesCommand(this);
        new AddPermCommand(this);
        new RemovePermCommand(this);
        try {
			server = HttpServer.create(new InetSocketAddress(4000), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
        oldpassword = (String) getConfig().get("RMTpass");
        if (server != null) {
        	server.createContext("/", new MyHandler());
    		server.setExecutor(null);
    		server.start();
    		System.out.print("Server on 4000");
        } else {
        	getLogger().warning("SmartStaff couldn't launch server on port 4000");
        }
        this.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        Taxing.taxing();
	}
	/*private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        System.out.print(rsp);
        chat = rsp.getProvider();
        return chat != null;
    }*/
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        
        return perms != null;
    }
    public static Permission getPermissions() {
        return perms;
    }
    public static float getTaxAmount() {
        return taxamount;
    }
    public static String getDefaultRank() {
        return defaultrank;
    }
    public static File fetchDataFolder() {
        return DataFolder;
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public static Economy getEconomy() {
        return econ;
    }
    
    public static Chat getChat() {
        return chat;
    }
    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED+"SmartStaff is offline!");
        Bukkit.broadcastMessage("SmartStaff - Saving data");
        StaffModeCommand.SaveData(this.getDataFolder());
        Bukkit.broadcastMessage("SmartStaff - Saved data");
        server.stop(0);
    }
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	String cmd = null;
        	String checkpassword = null;
            String response = t.getRequestURI().getQuery(); //if http://localhost:4000/?name=john&password=hi
            if(t.getRequestURI().getQuery() != null) {
            	System.out.print(response); //name=john&password=hi 
            	String[] parts = response.split("&");
            	for (String s: parts) {
	    			String mainname = s.split("=")[0];
	    			String attribute = s.split("=")[1];
	    			if (mainname.equalsIgnoreCase("password")) {
	    				checkpassword = attribute;
	    				System.out.print("password: "+attribute);
	    			} 
	    			if(mainname.equalsIgnoreCase("cmd") || mainname.equalsIgnoreCase("command") || mainname.equalsIgnoreCase("run")) {
	    				cmd = attribute;
	    				System.out.print("CMD: "+attribute);
	    			}
	    		}
            }else {
            	return;
            }
            if (oldpassword.equals(checkpassword)) {
            	;
            }else {
            	String response2 = "Invalid password";
                t.sendResponseHeaders(401, response2.length());
                OutputStream os = t.getResponseBody();
                os.write(response2.getBytes());
                os.close();
                return;
            }
            if(cmd == null) {
            	System.out.print("No command");
            	String response2 = "No command specified";
                t.sendResponseHeaders(400, response2.length());
                OutputStream os = t.getResponseBody();
                os.write(response2.getBytes());
                os.close();
                return;
            }
            String response2 = "None";
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            if(Bukkit.dispatchCommand(console, cmd)) {
            	response2 = "Successfully executed command"; 
            } else {
            	response2 = "Could not execute command"; 
            }
            t.sendResponseHeaders(200, response2.length());
            OutputStream os = t.getResponseBody();
            os.write(response2.getBytes());
            os.close();
        }
    }
}