package grokswell.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HLocation;

public class HyperToBukkit {

	
	public Location getLocation(HLocation hl){
		Location loc = new Location(Bukkit.getWorld(hl.getWorld()), hl.getX(), hl.getY(), hl.getZ());
		return loc;
	}
	
	public ItemStack getItemStack(HItemStack hi){
		Material mat = Material.getMaterial(hi.getMaterial());
		ItemStack stack = new ItemStack(mat, hi.getAmount(), hi.getDurability());
		return stack;
	}
}
