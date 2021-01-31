package tk.ifutureserver.fluxedscript.smartstaff;


import com.sun.net.httpserver.HttpServer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.Help;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.staff.RemoveStaff;
import tk.ifutureserver.fluxedscript.smartstaff.commands.staffmode.minis.staff.getStaff;
import tk.ifutureserver.fluxedscript.smartstaff.events.ChestOpen;
import tk.ifutureserver.fluxedscript.smartstaff.events.PlayerInteract;
import tk.ifutureserver.fluxedscript.smartstaff.events.PlayerJoin;
import tk.ifutureserver.fluxedscript.smartstaff.util.Taxing;
import tk.ifutureserver.fluxedscript.smartstaff.webpanel.IndexPage;
import tk.ifutureserver.fluxedscript.smartstaff.webpanel.api.economy.getEcoList;
import tk.ifutureserver.fluxedscript.smartstaff.webpanel.api.economy.getEcoUser;
import tk.ifutureserver.fluxedscript.smartstaff.webpanel.api.groups.getPermGroup;
import tk.ifutureserver.fluxedscript.smartstaff.webpanel.api.groups.getPermUser;
import tk.ifutureserver.fluxedscript.smartstaff.webpanel.api.players.getUser;
import tk.ifutureserver.fluxedscript.smartstaff.webpanel.cmdExecute;

import java.io.*;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.logging.Logger;

@SuppressWarnings("SpellCheckingInspection")
public class Main extends JavaPlugin implements Listener {
	public static  Logger log = Logger.getLogger("Minecraft");
	private static Permission perms = null;
    private static Economy econ = null;
    private static float taxamount = 1;
    public HttpServer server = null;
    public static String oldpassword = null;
    public static String oldapipassword = null;
    public static String defaultrank = null;
    public static File DataFolder = null;
    private static MemoryConfiguration config;
    private static Main instance;
    public static Main getInstance(){
        return instance;
    }

	@Override
    public void onEnable() {
        config = getConfig();

		instance = this;
		if (!setupPermissions() ) {
            log.severe("SmartStaff - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        File LangFile = new File (Bukkit.getServer().getPluginManager().getPlugin("SmartStaff").getDataFolder(), "messages.yml");
        if (!(LangFile.exists())){
            FileConfiguration LangMsgs = YamlConfiguration.loadConfiguration(LangFile);
            LangMsgs.set("HackerBan","I'm sorry you exist on an anti raid database for %reason%. Please appeal at https://discord.gg/UG5vD9d");
            LangMsgs.set("prefix","&c[&4SmartStaff&c]&f≫&r ");
            LangMsgs.set("StaffMode","%player% &cis now in &4&lSTAFF MODE");
            LangMsgs.set("RpMode","%player% &cis now in &b&lRP MODE");
            LangMsgs.set("NoPerm","You don't have perms!");
            try {
                LangMsgs.save(LangFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileUtils.copyInputStreamToFile(this.getResource("html/index.html"), new File(Bukkit.getServer().getPluginManager().getPlugin("SmartStaff").getDataFolder(),"html/index.html"));
                FileUtils.copyInputStreamToFile(this.getResource("html/errorpages/404.html"), new File(Bukkit.getServer().getPluginManager().getPlugin("SmartStaff").getDataFolder(), "html/errorpages/404.html"));

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
		setupEconomy();
		StaffData.LoadData(this.getDataFolder());
        System.out.print(Bukkit.getServer().getClass().getPackage().getName());
		//setupChat();
        saveDefaultConfig();
        if (getConfig().get("RMTpass").equals("LinuxRulesDonald123213123")){
            char[] possibleCharacters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$^&*()-_=+[|;:,<.>/?").toCharArray();
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
        //new BanCommand(this);
        this.registerCommands();
        Server(); //Start web server
        this.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new ChestOpen(), this);
        Taxing.taxing();
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


    public static MemoryConfiguration config(){
        return config;
    }
    public void Server(){
        int port = Main.config().getInt("port");
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print(e.getCause().toString());
        }
        oldpassword = (String) Main.config().get("RMTpass");
        oldapipassword = (String) Main.config().get("APIpass");
        if (server != null) {
            server.createContext("/api/groups/getuser", new getPermUser());
            server.createContext("/api/groups/getgroup", new getPermGroup());
            server.createContext("/api/economy/getuser", new getEcoUser());
            server.createContext("/api/economy/getlist", new getEcoList());
            server.createContext("/api/players/getuser", new getUser());
            server.createContext("/execute/", new cmdExecute());
            server.createContext("/", new IndexPage());

            //server.createContext("/static", new StaticFileServer());
            server.setExecutor(null);
            server.start();
            System.out.print("Server on "+port);
        } else {
            getLogger().warning("SmartStaff couldn't launch server on port "+port);
        }

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
        handler.register("stafflist", new getStaff());
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

        handler.register("help",new Help());

        getCommand("staffmode").setExecutor(handler);
        getCommand("staffmode").setTabCompleter(new TabCompletion());
    }

}

