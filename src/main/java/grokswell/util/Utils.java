package grokswell.util;

//import static java.lang.System.out;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;


public class Utils {
	private final static HashMap<String, String> special_chars = new HashMap<String, String>();
	static {
		special_chars.put("\\n", "\n");
		special_chars.put("\\<", "<");
		special_chars.put("\\>", ">");
		special_chars.put("\\,", ",");
		special_chars.put("\\;", ":");
		special_chars.put("§0 ", "§0");
		special_chars.put("§1 ", "§1");
		special_chars.put("§2 ", "§2");
		special_chars.put("§3 ", "§3");
		special_chars.put("§4 ", "§4");
		special_chars.put("§5 ", "§5");
		special_chars.put("§6 ", "§6");
		special_chars.put("§7 ", "§7");
		special_chars.put("§8 ", "§8");
		special_chars.put("§9 ", "§9");
		special_chars.put("§a ", "§a");
		special_chars.put("§b ", "§b");
		special_chars.put("§c ", "§c");
		special_chars.put("§d ", "§d");
		special_chars.put("§e ", "§e");
		special_chars.put("§f ", "§f");
	}

	public static String LocToString(Location l) {
		String stringloc = l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
		return stringloc;
	}

	public static Location StringToLoc(String s) {
		String[] sa = s.split(",");
		World w= Bukkit.getServer().getWorld(sa[0]);
		Location loc = new Location(w,Integer.parseInt(sa[1]),Integer.parseInt(sa[2]),Integer.parseInt(sa[3]));
		return loc;
	}
	
	public static Location getFirstBlockAboveGround(Location l){
		Location ag = l;
		while (ag.getBlock().getType() != Material.AIR) {
			ag = ag.add(0,1,0);
		}
		return ag;
	}
	
	public static String getPlayerInput(String input) {
		return "INPUT";
	}
	
	public static String formatText(String input, HashMap<String, String> keywords) {
		String text = input;
		String text2 = text;
		if (keywords != null) {
			for (String keyword: keywords.keySet()) {
				if (input.contains(keyword)) {
					text2 = text2.replace(keyword, keywords.get(keyword));
				}
			}
		}
		for (String c: special_chars.keySet()){
			if (text.contains(c)) {
				text2 = text2.replace(c, special_chars.get(c));
			}
		}
		return text2;
	}
}
