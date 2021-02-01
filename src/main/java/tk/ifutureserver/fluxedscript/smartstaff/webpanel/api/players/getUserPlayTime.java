package tk.ifutureserver.fluxedscript.smartstaff.webpanel.api.players;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.ifutureserver.fluxedscript.smartstaff.Main;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class getUserPlayTime implements HttpHandler {
    public static String getTime(long seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days + " days " + hours % 24 + " hours " + minutes % 60 + " minutes " + seconds % 60 + " seconds";
    }

    public static String getShortTime(long seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days + " days " + hours % 24 + " hours " + minutes % 60 + " minutes";
    }

    public Long findFile(String uuid) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("world/stats/" + uuid + ".json"));
            JSONObject jsonObject = (JSONObject) obj;
            return Long.parseLong(jsonObject.get("stat.playOneMinute").toString()) / 20;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    @SuppressWarnings("unchecked")
    public void handle(HttpExchange t) throws IOException {
        String oldpassword = Main.oldapipassword;
        String user = null;
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
                if(mainname.equalsIgnoreCase("name") || mainname.equalsIgnoreCase("username")) {
                    user = attribute;
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
            if(user != null) {
                @SuppressWarnings("deprecation") OfflinePlayer player = Bukkit.getOfflinePlayer(user);
                if (player == null){
                    String response2 = "Invalid username";
                    t.sendResponseHeaders(400, response2.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response2.getBytes());
                    os.close();
                    return;
                }
                Long Timeseconds = findFile(player.getUniqueId().toString());// get seconds
                JSONObject json_obj = new JSONObject();
                json_obj.put("success", true);
                json_obj.put("uuid", player.getUniqueId());
                json_obj.put("last_played", player.getLastPlayed());
                json_obj.put("first_played", player.getFirstPlayed());
                json_obj.put("time_played", getTime(Timeseconds));
                // do something with the request parameters
                final String responseBody = json_obj.toString();
                t.getResponseHeaders().set("Content-Type", "application/json");
                final byte[] rawResponseBody = responseBody.getBytes(StandardCharsets.UTF_8);
                t.sendResponseHeaders(200, rawResponseBody.length);
                t.getResponseBody().write(rawResponseBody);
            }
        }
    }
}