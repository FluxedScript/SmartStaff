package tk.ifutureserver.fluxedscript.smartstaff.webpanel.api.players;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.ifutureserver.fluxedscript.smartstaff.Main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class getPlayTimeTop implements HttpHandler {
    static Map<Long, String> users = new TreeMap<>(Collections.reverseOrder());

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

    @SuppressWarnings("unused")
    public ArrayList<String> getTop() {
        File dir = new File("world/stats/");
        File[] directoryListing = dir.listFiles();
        HashMap<Long, String> tempusers = new HashMap<>();
        ArrayList<String> leaderboard = new ArrayList<>();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(new FileReader("world/stats/" + child.getName()));
                    JSONObject jsonObject = (JSONObject) obj;
                    Long seconds = Long.parseLong(jsonObject.get("stat.playOneMinute").toString()) / 20;
                    tempusers.put(seconds,
                            Bukkit.getOfflinePlayer(UUID.fromString(child.getName().replace(".json", ""))).getName());
                } catch (Exception e) {
                    System.out.print("Looks like the following error came from a bad user file!");
                    System.out.print("Looks like the following error came from a bad user file!");
                    e.printStackTrace();
                    System.out.print("Looks like the following error came from a bad user file!");
                    System.out.print("Looks like the following error came from a bad user file!");
                }
            }
            users.putAll(tempusers);

            int count = 0;
            for (Map.Entry<Long, String> entry : users.entrySet()) {
                count ++;
                Long key = entry.getKey();
                String value = entry.getValue();
                leaderboard.add("" + count + ". " + value + ", " + getShortTime(key));
            }
            return leaderboard;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public void handle(HttpExchange t) throws IOException {
        String oldpassword = Main.oldapipassword;
        String checkpassword = null;
        String response = t.getRequestURI().getQuery(); //if http://localhost:4000/api?name=john&password=hi
        if (t.getRequestURI().getQuery() != null) {
            String[] parts = response.split("&");
            for (String s : parts) {
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
            json_obj.put("success", true);
            json_obj.put("top_users", getTop());
            // do something with the request parameters
            final String responseBody = json_obj.toString();
            t.getResponseHeaders().set("Content-Type", "application/json");
            final byte[] rawResponseBody = responseBody.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, rawResponseBody.length);
            t.getResponseBody().write(rawResponseBody);
        }
    }
}