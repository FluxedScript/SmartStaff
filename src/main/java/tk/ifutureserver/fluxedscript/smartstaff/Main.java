package tk.ifutureserver.fluxedscript.smartstaff;

import com.sun.net.httpserver.HttpServer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import tk.ifutureserver.fluxedscript.smartstaff.commands.economy.TaxUser;
import tk.ifutureserver.fluxedscript.smartstaff.commands.misc.HelloCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.playtime.PlayTimeCommand;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.CommandHandler;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffData;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.StaffMode;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.TabCompletion;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.perms.AddPerm;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.perms.RemovePerm;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.roles.CreateRole;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.roles.DeleteRole;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.roles.UpdateRole;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.roles.ViewRole;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.staff.AddStaff;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.staff.RemoveStaff;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.staff.getStaff;
import tk.ifutureserver.fluxedscript.smartstaff.events.PlayerInteract;
import tk.ifutureserver.fluxedscript.smartstaff.events.PlayerJoin;
import tk.ifutureserver.fluxedscript.smartstaff.util.Taxing;
import tk.ifutureserver.fluxedscript.smartstaff.webpanel.HomePage;
import tk.ifutureserver.fluxedscript.smartstaff.webpanel.api.ApiHome;
import tk.ifutureserver.fluxedscript.smartstaff.webpanel.cmdExecute;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("SpellCheckingInspection")
public class Main extends JavaPlugin implements Listener {
	public static  Logger log = Logger.getLogger("Minecraft");
	private static Permission perms = null;
    private static Economy econ = null;
    private static float taxamount = 1;
    public static String oldpassword = null;
    public static String oldapipassword = null;
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
		StaffData.LoadData(this.getDataFolder());

		//setupChat();
		getConfig().options().copyDefaults(true);
		saveConfig();
        if (getConfig().get("RMTpass").equals("LinuxRulesDonald123213123")){
            char[] possibleCharacters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?").toCharArray();
            String randomPass = RandomStringUtils.random( (int)(Math.random() * (30 - 10 + 1) + 10), 0, possibleCharacters.length-1, false, false, possibleCharacters, new SecureRandom() );
            String randomPass2 = RandomStringUtils.random( (int)(Math.random() * (30 - 10 + 1) + 10), 0, possibleCharacters.length-1, false, false, possibleCharacters, new SecureRandom() );
            getConfig().set("RMTpass",randomPass);
            getConfig().set("APIpass",randomPass2);
            saveConfig();
        }
		taxamount = (float)getConfig().getDouble("taxes");
		defaultrank = getConfig().getString("DefaultRank");
		DataFolder = getDataFolder();
        getLogger().info("SmartStaff is online!");
        new HelloCommand(this);
        new TaxUser(this);
        new PlayTimeCommand(this);

        this.registerCommands();
        StartWeb(); //Start web server
        this.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        Taxing.taxing();
	}
    private void StartWeb(){
        int port = 4000;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print(e.getCause());
        }
        oldpassword = (String) getConfig().get("RMTpass");
        oldapipassword = (String) getConfig().get("APIpass");
        if (server != null) {
            server.createContext("/", new HomePage());
            server.createContext("/execute", new cmdExecute());
            server.createContext("/api/", new ApiHome());
            server.setExecutor(null);
            server.start();
            System.out.print("Server on "+port);
        } else {
            getLogger().warning("SmartStaff couldn't launch server on port "+port);
        }
    }
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

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "SmartStaff is offline!");
        Bukkit.broadcastMessage("SmartStaff - Saving data");
        StaffData.SaveData(this.getDataFolder());
        Bukkit.broadcastMessage("SmartStaff - Saved data");
        server.stop(0);
    }

    //Because there may be LOTS of commands, we are going to make a separate method to keep
    //the onEnable() nice and clean.
    public void registerCommands() {
        CommandHandler handler = new CommandHandler();

        //Registers the command /staffmode which has no arguments.
        handler.register("staffmode", new StaffMode());

        //Registers the command /staffmode args based on args[0] (args)
        handler.register("liststaff", new getStaff());
        handler.register("viewstaff", new getStaff());

        handler.register("addstaff", new AddStaff());
        handler.register("hirestaff", new AddStaff());
        handler.register("hire", new AddStaff());

        handler.register("removestaff", new RemoveStaff());
        handler.register("firestaff", new RemoveStaff());
        handler.register("fire", new RemoveStaff());


        handler.register("addperm", new AddPerm());
        handler.register("permadd", new AddPerm());

        handler.register("removeperm", new RemovePerm());
        handler.register("delperm", new RemovePerm());
        handler.register("permremove", new RemovePerm());


        handler.register("createrole",new CreateRole());

        handler.register("deleterole",new DeleteRole());
        handler.register("delrole",new DeleteRole());

        handler.register("updaterole",new UpdateRole());
        handler.register("viewroles",new ViewRole());
        getCommand("staffmode").setExecutor(handler);
        getCommand("staffmode").setTabCompleter(new TabCompletion());
    }
}