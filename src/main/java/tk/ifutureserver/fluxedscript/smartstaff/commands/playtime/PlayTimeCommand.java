package tk.ifutureserver.fluxedscript.smartstaff.commands.playtime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.ifutureserver.fluxedscript.smartstaff.Main;
import tk.ifutureserver.fluxedscript.smartstaff.util.Numbers;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;

public class PlayTimeCommand implements CommandExecutor {
	static Map<Long, String> users = new TreeMap<>(Collections.reverseOrder());

	public PlayTimeCommand(Main plugin) {
		plugin.getCommand("playtime").setExecutor(this);
	}

	public OfflinePlayer getOfflinePlayer(String name) {
		try {
			for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
				if (player.getName().equals(name))
					return player;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public Player getOnlinePlayer(String name) {
		try {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getName().equals(name))
					return player;
			}
			return null; // Not online
		} catch (Exception e) {
			return null;
		}
	}

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
	public ArrayList<String> getTop(int pagenum) {
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
					e.printStackTrace();
				}
			}
			users.putAll(tempusers);

			int pages = 1;
			int records = 0;
			for (Entry<Long, String> s : users.entrySet()) {
				records++;
				if ((records % 12) == 0) {
					pages++;
				}
			}
			final String e = ChatColor.YELLOW + "------ " + ChatColor.GOLD + "PlayTimes" + ChatColor.YELLOW + " -- "
					+ ChatColor.GOLD + "Page " + ChatColor.RED + pagenum + ChatColor.GOLD + "/" + ChatColor.RED + pages
					+ ChatColor.GOLD + "----";
			leaderboard.add(e);
			int count = 0;
			for (Map.Entry<Long, String> entry : users.entrySet()) {
				count++;
				Long key = entry.getKey();
				String value = entry.getValue();
				if (count == 1) {
					leaderboard.add(ChatColor.AQUA + "" + count + ". " + value + ", " + getShortTime(key));
				} else if (count == 2) {
					leaderboard.add(ChatColor.RED + "" + count + ". " + value + ", " + getShortTime(key));
				} else if (count == 3) {
					leaderboard.add(ChatColor.GREEN + "" + count + ". " + value + ", " + getShortTime(key));
				} else {
					leaderboard.add(ChatColor.GREEN + "" + count + ". " + value + ", " + getShortTime(key));
				}
				if (count == (12 * pagenum)) {
					leaderboard.add(ChatColor.GOLD + "Type " + ChatColor.RED + "/playtime top " + (pagenum + 1) + ChatColor.GOLD
							+ " to read the next page!");
					break;
				} else if (count == (12 * (pagenum - 1))) {
					leaderboard.removeAll(leaderboard);
					leaderboard.add(e);
				}
			}

			return leaderboard;
		} else {
			return null;
		}
	}



	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players may execute this command!");
			return true;
		}
		Player player = (Player) sender;
		if (args.length > 1) {
			if (!(Numbers.isNumeric(args[1]))) {
				sender.sendMessage("Invalid argument");
				return true;
			}
		}
		if (args.length == 0) { // for self
			if (player.hasPermission("ss.playtime.self")) {
				long Timeseconds = player.getStatistic(Statistic.PLAY_ONE_TICK) / 20; // get seconds
				player.sendMessage("You have played for " + getTime(Timeseconds));
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
			}
			return true;
		} else if (args.length > 2) { // too many args
			sender.sendMessage("Too many arguments");
			return true;
		} else if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("top")) {
			if (player.hasPermission("ss.playtime.top")) {
				if (args.length < 2) {
					player.sendMessage(
							ChatColor.GOLD + "Top users this month:\n" + ChatColor.BLUE + String.join("\n", getTop(1)));
				} else {
					player.sendMessage(ChatColor.GOLD + "Top users this month:\n" + ChatColor.BLUE
							+ String.join("\n", getTop(Integer.parseInt(args[1]))));
				}

			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
			}
			return true;
		} else {
			if (!(player.hasPermission("ss.playtime.others"))) {
				player.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
				return true;
			}
			Player user = getOnlinePlayer(args[0]);
			if (user == null) {
				OfflinePlayer offlineuser = getOfflinePlayer(args[0]);
				if (offlineuser == null) {
					sender.sendMessage("This player has never played before!");
				} else {
					Long Timeseconds = findFile(offlineuser.getUniqueId().toString());// get seconds
					player.sendMessage(offlineuser.getName() + " Has played for " + getTime(Timeseconds));
				}
			} else {
				long Timeseconds = user.getStatistic(Statistic.PLAY_ONE_TICK) / 20;
				player.sendMessage(user.getName() + " Has played for " + getTime(Timeseconds));
			}
			return true;

		}
	}
}