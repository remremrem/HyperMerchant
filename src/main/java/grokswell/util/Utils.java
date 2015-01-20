package grokswell.util;

//import static java.lang.System.out;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Utils {

	public String LocToString(Location l) {
		String stringloc = l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
		return stringloc;
	}

	public Location StringToLoc(String s) {
		String[] sa = s.split(",");
		World w= Bukkit.getServer().getWorld(sa[0]);
		Location loc = new Location(w,Integer.parseInt(sa[1]),Integer.parseInt(sa[2]),Integer.parseInt(sa[3]));
		return loc;
	}
	
	public Location getFirstBlockAboveGround(Location l){
		Location ag = l;
		while (ag.getBlock().getType() != Material.AIR) {
			ag = ag.add(0,1,0);
		}
		return ag;
	}
	
	public String getPlayerInput(String input) {
		return "INPUT";
	}
}
