package tk.ifutureserver.fluxedscript.smartstaff.webpanel.api.players;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import tk.ifutureserver.fluxedscript.smartstaff.Main;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class getOnlinePlayers implements HttpHandler {
    Economy eco = Main.getEconomy();
    Permission perms = Main.getPermissions();
    @SuppressWarnings("unchecked")
    public void handle(HttpExchange t) throws IOException {
        String oldpassword = Main.oldapipassword;
        String checkpassword = null;
        String response = t.getRequestURI().getQuery(); //if http://localhost:4000/api?name=john&password=hi
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
            JSONObject json_obj = new JSONObject();
            for (Player player : Bukkit.getOnlinePlayers()){
                JSONObject mini_json = new JSONObject();
                String[] permList = perms.getPlayerGroups(null, player);
                List<String> list = new ArrayList<>(Arrays.asList(permList));
                JSONArray array2 = new JSONArray();
                array2.addAll(list);
                long balance = (long) eco.getBalance(player);
                mini_json.put("success", true);
                mini_json.put("uuid", player.getUniqueId());
                mini_json.put("last_played", player.getLastPlayed());
                mini_json.put("first_played", player.getFirstPlayed());
                mini_json.put("whitelisted", player.isWhitelisted());
                mini_json.put("is_banned", player.isBanned());
                mini_json.put("is_op", player.isOp());
                mini_json.put("groups", array2);
                mini_json.put("balance", balance);
                json_obj.put(player.getName(), mini_json);
            }
            // do something with the request parameters
            final String responseBody = json_obj.toString();
            t.getResponseHeaders().set("Content-Type", "application/json");
            final byte[] rawResponseBody = responseBody.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, rawResponseBody.length);
            t.getResponseBody().write(rawResponseBody);
        }
    }
}
