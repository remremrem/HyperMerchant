package grokswell.hypermerchant;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Utils {

	public String LocToString(Location l) {
		String stringloc = l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
		return stringloc;
	}

	public Location StringToLoc(String s) {
		String[] sa = s.split(",");
		World w= Bukkit.getServer().getWorld(sa[0]);
		Location loc = new Location(w,Integer.getInteger(sa[1]),Integer.getInteger(sa[2]),Integer.getInteger(sa[3]));
		return loc;
	}
}
