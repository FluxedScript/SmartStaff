package tk.ifutureserver.fluxedscript.smartstaff.webpanel.api.economy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import tk.ifutureserver.fluxedscript.smartstaff.Main;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class getEcoList implements HttpHandler {
    Economy eco = Main.getEconomy();

    @SuppressWarnings("unchecked")
    public void handle(HttpExchange t) throws IOException {
        String oldpassword = Main.oldapipassword;
        String checkpassword = null;
        String response = t.getRequestURI().getQuery(); //if http://localhost:4000/api?name=john&password=hi

        String responseBody;

        if(t.getRequestURI().getQuery() != null) {
            String[] parts = response.split("&");
            for (String s: parts) {
                String mainname = s.split("=")[0];
                String attribute = s.split("=")[1];
                if (mainname.equalsIgnoreCase("password")) {
                    checkpassword = attribute;
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
            JSONObject main_json_obj = new JSONObject();
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()){
                JSONObject json_obj = new JSONObject();
                long balance = (long) eco.getBalance(player);
                json_obj.put("success", true);
                json_obj.put("name", player.getName());
                json_obj.put("uuid", player.getUniqueId());
                json_obj.put("last_played", player.getLastPlayed());
                json_obj.put("balance", balance);
                main_json_obj.put(player.getUniqueId(), json_obj);
            }
            responseBody = main_json_obj.toString();
            // do something with the request parameters

            t.getResponseHeaders().set("Content-Type", "application/json");
            final byte[] rawResponseBody = responseBody.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, rawResponseBody.length);
            t.getResponseBody().write(rawResponseBody);
        }

    }

}
