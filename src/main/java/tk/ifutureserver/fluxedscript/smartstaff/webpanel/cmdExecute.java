package tk.ifutureserver.fluxedscript.smartstaff.webpanel;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import tk.ifutureserver.fluxedscript.smartstaff.Main;

import java.io.IOException;
import java.io.OutputStream;

public class cmdExecute implements HttpHandler {
    static String oldpassword = Main.oldpassword;
    public void handle(HttpExchange t) throws IOException {
        String cmd = null;
        String checkpassword = null;
        String response = t.getRequestURI().getQuery(); //if http://localhost:4000/?name=john&password=hi
        if(t.getRequestURI().getQuery() != null) {
            System.out.print(t.getRequestURI().getPath());
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
                }
            }
        }else {
            return;
        }
        for (Object block_cmd :Main.getInstance().getConfig().getList("BlockedCmds")){
            if (String.valueOf(block_cmd).equalsIgnoreCase(cmd)){
                String response2 = "Blocked command!";
                t.sendResponseHeaders(401, response2.length());
                OutputStream os = t.getResponseBody();
                os.write(response2.getBytes());
                os.close();
                return;
            }
        }
        if (!(oldpassword.equals(checkpassword))) {
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
        String response2;
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